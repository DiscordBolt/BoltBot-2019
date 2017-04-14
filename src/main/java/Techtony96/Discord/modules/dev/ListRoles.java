package Techtony96.Discord.modules.dev;

import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.BotCommand;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Tony on 2/25/2017.
 */
public class ListRoles {

    @BotCommand(command = "ListRoles", description = "List the roles of the guild and their ID.", usage = "!ListRoles", permissions = Permissions.MANAGE_ROLES)
    public static void listRolesCommand(CommandContext cc) {
        StringBuilder sb = new StringBuilder(String.format("|%-20s|%-18s|%n", "Role", "ID"));
        sb.append("+--------------------+------------------+\n");
        for (IRole r : cc.getGuild().getRoles()) {
            sb.append(String.format("|%-20s|%-18s|%n", r.getName(), r.getID()));
        }
        cc.replyWith("```\n" + sb.toString() + "```");
    }
}
