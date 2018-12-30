package com.discordbolt.boltbot.discord.modules.administration;

import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CustomCommand;
import com.discordbolt.boltbot.discord.api.CommandBean;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.GuildRepository;
import discord4j.core.object.util.Permission;

public class CommandPrefixCommand extends CustomCommand {

    private static final String[] command = {"prefix"};
    private static final String description = "Change command prefix";
    private static final String usage = "Prefix <prefix>";
    private static final String module = "Administration";

    public CommandPrefixCommand() {
        super(command, description, usage, module);
        super.setPermissions(Permission.ADMINISTRATOR);
        super.setArgumentCount(2);
    }

    @Override
    public void execute(CommandContext commandContext) {
        String newPrefix = commandContext.getArguments().get(1);

        GuildRepository guildRepository = BeanUtil.getBean(GuildRepository.class);

        commandContext.getGuild().flatMap(guild -> guildRepository.findById(guild.getId()))
                .map(data -> data.setCommandPrefix(newPrefix))
                .flatMap(guildRepository::save)
                .doOnNext(guildData -> BeanUtil.getBean(CommandBean.class).setCommandPrefix(guildData.getId(), guildData.getCommandPrefix()))
                .flatMap(data -> commandContext.replyWith("Successfully set command prefix to `" + data.getCommandPrefix() + "`.\nPrefix all commands with `" + data.getCommandPrefix() + "`."))
                .subscribe();
    }
}
