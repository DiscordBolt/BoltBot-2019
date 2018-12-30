package com.discordbolt.boltbot.discord.modules.administration;

import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CustomCommand;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.util.Permission;

public class ListRolesCommand extends CustomCommand {

    private static final String[] command = {"listroles"};
    private static final String description = "View all roles";
    private static final String usage = "ListRoles";
    private static final String module = "Administration";

    public ListRolesCommand() {
        super(command, description, usage, module);
        super.setPermissions(Permission.MANAGE_ROLES);
    }

    @Override
    public void execute(CommandContext commandContext) {
        commandContext.getGuild()
                .flatMapMany(Guild::getRoles)
                .collectList()
                .map(roleList -> {
                    StringBuilder sb = new StringBuilder(String.format("|%-20s|%-18s|%n", "Role", "ID"));
                    sb.append("+--------------------+------------------+\n");
                    for (Role r : roleList) {
                        sb.append(String.format("|%-20s|%-18s|%n", r.getName(), r.getId().asString()));
                    }
                    return "```" + sb.toString() + "```";
                })
                .flatMap(commandContext::replyWith)
                .subscribe();
    }
}
