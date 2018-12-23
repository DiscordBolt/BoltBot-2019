package com.discordbolt.boltbot.discord.modules.seen;

import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CustomCommand;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.UserRepository;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
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
                .map(member -> Tuples.of(member, userRepository.findById(member.getId())))
                .flatMap(tuple -> commandContext.replyWith(tuple.getT1().getDisplayName() + " was last online " + tuple.getT2().get().getLastOnline().toString()))
                .switchIfEmpty(commandContext.replyWith("Unable to find that user."))
                .subscribe();
    }
}
