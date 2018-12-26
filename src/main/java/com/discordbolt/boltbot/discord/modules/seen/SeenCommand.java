package com.discordbolt.boltbot.discord.modules.seen;

import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CustomCommand;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.discord.util.TimeUtil;
import com.discordbolt.boltbot.repository.UserRepository;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.presence.Status;
import reactor.util.function.Tuples;

public class SeenCommand extends CustomCommand {

    private static final String[] command = {"seen"};
    private static final String description = "See when a user was last online";
    private static final String usage = "Seen Username";
    private static final String module = "Seen";

    private DiscordClient client;
    private UserRepository userRepository;

    public SeenCommand(DiscordClient client) {
        super(command, description, usage, module);
        super.setMinArgumentCount(2);
        this.client = client;
        this.userRepository = BeanUtil.getBean(UserRepository.class);
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.getMessage().getUserMentions()
                .zipWith(commandContext.getGuild())
                .flatMap(t -> t.getT1().asMember(t.getT2().getId()))
                .switchIfEmpty(commandContext.getGuild().flatMapMany(Guild::getMembers)
                        .filter(m -> m.getDisplayName().equalsIgnoreCase(commandContext.combineArgs(1, commandContext.getArgCount() - 1)) || m.getUsername().equalsIgnoreCase(commandContext.combineArgs(1, commandContext.getArgCount() - 1)))
                        .next())
                .map(member -> Tuples.of(member, userRepository.findById(member.getId()), member.getPresence()))
                .flatMap(tuple -> tuple.getT2().map(t -> Tuples.of(tuple.getT1(), t, tuple.getT3())))
                .flatMap(tuple -> tuple.getT3().map(t -> Tuples.of(tuple.getT1(), tuple.getT2(), t)))
                .flatMap(tuple -> {
                    if (tuple.getT3().getStatus() == Status.ONLINE)
                        return commandContext.replyWith(tuple.getT1().getDisplayName() + " is currently online!");
                    else
                        return commandContext.replyWith(tuple.getT1().getDisplayName() + " was last online " + TimeUtil.timeAgo(tuple.getT2().getLastOnline()) + " ago.");
                })
                .switchIfEmpty(commandContext.replyWith("Unable to find that user.")) //This is called if the user has never been online
                .subscribe();
    }
}
