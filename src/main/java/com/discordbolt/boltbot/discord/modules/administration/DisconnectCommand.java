package com.discordbolt.boltbot.discord.modules.administration;

import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CustomCommand;
import com.discordbolt.api.commands.exceptions.CommandStateException;
import com.discordbolt.boltbot.discord.system.botlog.BotLog;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import reactor.core.publisher.Mono;

public class DisconnectCommand extends CustomCommand {

    private static final String[] command = {"disconnect"};
    private static final String description = "Disconnect users(s) from their voice channel";
    private static final String usage = "Disconnect @User1 @User2";
    private static final String module = "Administration";

    private DiscordClient client;

    public DisconnectCommand(DiscordClient client) {
        super(command, description, usage, module);
        super.setMinArgumentCount(2);
        super.setPermissions(Permission.MOVE_MEMBERS);
        super.setBotRequiredPermissions(Permission.MOVE_MEMBERS, Permission.MANAGE_CHANNELS);
        this.client = client;
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.getMessage().getUserMentions()
                .switchIfEmpty(Mono.just(commandContext.findMembers(1).get(0)))
                .flatMap(user -> commandContext.getGuild().flatMap(guild -> user.asMember(guild.getId())))
                .filterWhen(member -> member.getVoiceState().map(voiceState -> voiceState.getChannelId().isPresent()))
                .switchIfEmpty(Mono.error(new CommandStateException("No specified members were connected to a voice channel.")))
                .zipWith(commandContext.getGuild().flatMap(guild -> guild.createVoiceChannel(spec -> spec.setName("disconnect"))))
                .flatMap(tuple -> tuple.getT1().edit(spec -> spec.setNewVoiceChannel(tuple.getT2().getId())).thenReturn(tuple.getT2()))
                .flatMap(Channel::delete)
                .doOnComplete(() -> BotLog.logAction(commandContext.getGuild(), String.format("%s just disconnected users %s", commandContext.getUser().block().getUsername(), String.join(", ", commandContext.getMessage().getUserMentions().map(User::getUsername).collectList().block()))))
                .subscribe(t -> {
                }, error -> commandContext.replyWith(error.getMessage()).subscribe());
    }
}
