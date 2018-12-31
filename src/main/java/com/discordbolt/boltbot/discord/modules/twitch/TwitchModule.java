package com.discordbolt.boltbot.discord.modules.twitch;

import com.discordbolt.api.commands.BotCommand;
import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.modules.twitch.responses.ChannelData;
import com.discordbolt.boltbot.discord.modules.twitch.responses.UserData;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TwitchModule implements BotModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchModule.class);
    private static final long COOLDOWN_PERIOD_MS = TimeUnit.MINUTES.toMillis(30); //30 Minute cooldown

    private DiscordClient client;
    private HashMap<Long, Instant> cooldowns = new HashMap<>(); //Hashmap of UserIDs and times when cooldowns expire

    @Override
    public void initialize(DiscordClient client) {
        this.client = client;
        client.getEventDispatcher()
                .on(PresenceUpdateEvent.class)
                .filter(e -> e.getCurrent().getActivity().map(Activity::getType).orElse(null) == Activity.Type.PLAYING)
                .filter(e -> e.getCurrent().getActivity().map(Activity::getType).map(Activity.Type.PLAYING::equals).orElse(false))
                .doOnNext(e -> LOGGER.debug("User '" + e.getUserId().asString() + "' in guild '" + e.getGuildId().asString() + "' is now streaming."))
                .flatMap(this::announceChannel)
                .subscribe();
    }

    private Mono<Message> announceChannel(PresenceUpdateEvent a) {
        if (cooldowns.containsKey(a.getUserId().asLong()) && Instant.now().isBefore(cooldowns.get(a.getUserId().asLong())))
            return Mono.empty(); // cooldown period has not been passed yet
        return BeanUtil.getBean(GuildRepository.class)
                .findById(a.getGuildId())
                .map(GuildData::getStreamAnnounceChannel)
                .filter(id -> id != 0L)
                .map(Snowflake::of)
                .flatMap(channelId -> a.getGuild().flatMap(guild -> guild.getChannelById(channelId)))
                .ofType(TextChannel.class)
                .flatMap(channel -> {
                    String twitchUsername = a.getCurrent().getActivity()
                            .flatMap(Activity::getStreamingUrl)
                            .map(url -> url.substring(url.lastIndexOf('/')))
                            .orElse(null);

                    if (twitchUsername == null || twitchUsername.length() <= 0)
                        return Mono.empty();

                    LOGGER.debug("Announcing stream '" + twitchUsername + "' in guild '" + a.getGuildId().asString() + "'");
                    cooldowns.put(a.getUserId().asLong(), Instant.now().plusMillis(COOLDOWN_PERIOD_MS));

                    ChannelData channelData = BeanUtil.getBean(TwitchAPI.class).getChannelData(twitchUsername).getBody();
                    UserData userData = BeanUtil.getBean(TwitchAPI.class).getUserData(twitchUsername).getBody();
                    if (channelData == null || userData == null) {
                        LOGGER.error("Unable to get Twitch info for streamer '" + twitchUsername + "'. Falling back to default message.");
                        return a.getMember()
                                .map(Member::getNicknameMention)
                                .flatMap(mention -> channel.createMessage(mention + " just went live! <https://twitch.tv/" + twitchUsername + ">"));
                    } else {
                        return a.getMember().flatMap(member -> channel.createMessage(spec -> spec.setEmbed(EmbedBuilder.createEmbed(channelData, userData, member))));
                    }
                });
    }

    @BotCommand(command = "twitch", description = "Generate Twitch embed of given channel", usage = "Twitch <channel>", module = "Twitch", args = 2)
    public static void twitchCommand(CommandContext cc) {
        ResponseEntity<ChannelData> channelData = BeanUtil.getBean(TwitchAPI.class).getChannelData(cc.getArguments().get(1));
        ResponseEntity<UserData> userData = BeanUtil.getBean(TwitchAPI.class).getUserData(cc.getArguments().get(1));

        if (channelData.getBody() == null || channelData.getBody().dataIsEmpty() || userData.getBody() == null || userData.getBody().dataIsEmpty()) {
            cc.replyWith("Channel is offline.").subscribe();
        } else {
            cc.replyWith(EmbedBuilder.createEmbed(channelData.getBody(), userData.getBody(), null)).subscribe();
        }
    }

    @BotCommand(command = "twitchconfig", description = "Change the behavior of the Twitch module. Use 0 to disable streamer announcements.", usage = "TwitchConfig <#ChannelMention>", module = "Twitch", args = 2, permissions = Permission.ADMINISTRATOR)
    public static void twitchConfigCommand(CommandContext cc) {
        cc.getGuild()
                .map(guild -> Tuples.of(guild, BeanUtil.getBean(GuildRepository.class).findById(guild.getId())))
                .flatMap(tuple -> tuple.getT2().map(data -> Tuples.of(tuple.getT1(), data)))
                .flatMap(tuple -> {
                    if (cc.getArguments().get(1).equalsIgnoreCase("0"))
                        return Mono.just(tuple.getT2().setStreamAnnounceChannel(0));
                    else {
                        Snowflake channelRequested = Snowflake.of(cc.getArguments().get(1).replaceAll("[^\\d]", ""));
                        return tuple.getT1().getChannelById(channelRequested)
                                .ofType(TextChannel.class)
                                .map(Channel::getId)
                                .map(Snowflake::asLong)
                                .map(id -> tuple.getT2().setStreamAnnounceChannel(id));
                    }
                })
                .flatMap(data -> BeanUtil.getBean(GuildRepository.class).save(data))
                .flatMap(data -> cc.getMessage().getClient().getChannelById(Snowflake.of(data.getStreamAnnounceChannel())).onErrorResume(t -> Mono.empty()).ofType(TextChannel.class).map(TextChannel::getMention))
                .flatMap(channelMention -> cc.replyWith("Set stream announce channel to " + channelMention))
                .switchIfEmpty(cc.replyWith("Disabled stream announcements."))
                .subscribe();
    }
}
