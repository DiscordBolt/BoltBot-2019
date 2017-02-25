package Techtony96.Discord.modules.dev;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Tony on 2/25/2017.
 */
public class ListRoles extends BotCommand {

    public ListRoles(IDiscordClient client) {
        super(client, "listroles");
        setDescription("List the roles of the guild and their ID.");
        setUsage("!listroles");
        setArguments(1);
        setPermissions(Permissions.MANAGE_ROLES);
    }

    @Override
    public void execute(CommandContext cc) {

        StringBuilder sb = new StringBuilder(String.format("|%-20s|%-18s|%n", "Role", "ID"));
        sb.append("+--------------------+------------------+\n");
        for (IRole r : cc.getGuild().getRoles()) {
            sb.append(String.format("|%-20s|%-18s|%n", r.getName(), r.getID()));
        }
        cc.replyWith("```\n" + sb.toString() + "```");
    }
}
