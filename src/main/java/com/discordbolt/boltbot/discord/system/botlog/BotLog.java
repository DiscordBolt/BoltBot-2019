package com.discordbolt.boltbot.discord.system.botlog;

import com.discordbolt.boltbot.discord.api.BotModule;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.TextChannelCreateSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Set;

public class BotLog implements BotModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotLog.class);

    private static BotLog instance;
    private DiscordClient client;
    private HashMap<Long, TextChannel> loggingChannels = new HashMap<>();

    @Override
    public void initialize(DiscordClient client) {
        this.client = client;
        BotLog.instance = this;
        client.getEventDispatcher().on(GuildCreateEvent.class).subscribe(event -> setup(event.getGuild()));
    }

    public static void logAction(long guild, String message) {
        LOGGER.info("Guild: " + guild + " Message: " + message);
        if (instance.loggingChannels.containsKey(guild))
            instance.loggingChannels.get(guild).createMessage(message).subscribe();
    }

    public static void logAction(Mono<Guild> guild, String message) {
        guild.map(Guild::getId).subscribe(guildId -> logAction(guildId, message));
    }

    public static void logAction(Snowflake guild, String message) {
        logAction(guild.asLong(), message);
    }

    private void setup(Guild guild) {
        guild.getChannels().ofType(TextChannel.class)
                .filter(channel -> channel.getName().equals("bot-log"))
                .switchIfEmpty(guild.createTextChannel(createChannelSpec(guild.getId(), client.getSelfId().get())))
                .subscribe(textChannel -> loggingChannels.put(guild.getId().asLong(), textChannel), error -> LOGGER.warn("Unable to create #bot-log channel in " + guild.getId().asString() + ". Reason: " + error.getMessage()));
    }

    private TextChannelCreateSpec createChannelSpec(Snowflake guildId, Snowflake botId) {
        TextChannelCreateSpec spec = new TextChannelCreateSpec();
        spec.setName("bot-log");
        spec.setTopic("Bolt logging channel for administrative actions taken.");
        spec.setPermissionOverwrites(Set.of(
                PermissionOverwrite.forRole(guildId, PermissionSet.none(), PermissionSet.of(Permission.VIEW_CHANNEL)),
                PermissionOverwrite.forMember(botId, PermissionSet.of(Permission.VIEW_CHANNEL, Permission.SEND_MESSAGES), PermissionSet.none()))
        );
        return spec;
    }
}
