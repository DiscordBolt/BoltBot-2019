package com.discordbolt.boltbot.discord.modules.administration;

import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CustomCommand;
import com.discordbolt.api.commands.exceptions.CommandStateException;
import com.discordbolt.boltbot.discord.system.botlog.BotLog;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.VoiceChannel;
import discord4j.core.object.util.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

public class MoveCommand extends CustomCommand {

    private static final String[] command = {"move"};
    private static final String description = "Move users to voice channels";
    private static final String usage = "Move [@User/Current/All] [Channel/ID]";
    private static final String module = "Administration";


    public MoveCommand() {
        super(command, description, usage, module);
        super.setPermissions(Permission.MOVE_MEMBERS);
        super.setMinArgumentCount(3);
        //TODO Set ratelimit through command api
    }

    @Override
    public void execute(CommandContext commandContext) {

        String toChannel = commandContext.combineArgs(2, commandContext.getArgCount() - 1).trim();
        Flux<User> users;

        switch (commandContext.getArguments().get(1).toLowerCase()) {
            case "current":
                users = commandContext.getMember()
                        .flatMap(Member::getVoiceState)
                        .flatMap(VoiceState::getChannel)
                        .flatMapMany(VoiceChannel::getVoiceStates)
                        .flatMap(VoiceState::getUser);
                break;
            case "all":
                users = commandContext.getGuild()
                        .flatMapMany(Guild::getVoiceStates)
                        .flatMap(VoiceState::getUser);
                break;
            default:
                users = commandContext.getMessage().getUserMentions();
        }

        commandContext.getGuild().flatMapMany(Guild::getChannels)
                .ofType(VoiceChannel.class)
                .filter(voiceChannel -> voiceChannel.getName().equalsIgnoreCase(toChannel) || voiceChannel.getId().asString().equals(toChannel))
                .next()
                .switchIfEmpty(Mono.error(new CommandStateException("No voice channel was found matching '" + toChannel + "'.")))
                .flatMapMany(voiceChannel -> users.filterWhen(user -> user.asMember(voiceChannel.getGuildId()).flatMap(Member::getVoiceState).hasElement())
                        .map(member -> Tuples.of(member, voiceChannel)))
                .switchIfEmpty(Mono.error(new CommandStateException("No users were found to move.")))
                .flatMap(tuple -> tuple.getT1().asMember(tuple.getT2().getGuildId()).flatMap(member -> member.edit(spec -> spec.setNewVoiceChannel(tuple.getT2().getId()))))
                .flatMap(t -> commandContext.replyWith("Successfully moved all users."))
                .doOnComplete(() -> BotLog.logAction(commandContext.getGuild(), String.format("%s just moved users %s", commandContext.getUser().block().getUsername(), String.join(", ", users.map(User::getUsername).collectList().block()))))
                .subscribe(t -> {
                }, error -> commandContext.replyWith(error.getMessage()).subscribe());
    }
}
