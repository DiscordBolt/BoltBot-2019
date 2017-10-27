package net.ajpappas.discord.modules.dev;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Created by Tony on 2/25/2017.
 */
public class ListRoles {

    @BotCommand(command = "ListRoles", module = "dev", description = "List the roles of the guild and their ID.", usage = "ListRoles", permissions = Permissions.MANAGE_ROLES)
    public static void listRolesCommand(CommandContext cc) {
        StringBuilder sb = new StringBuilder("```");

        for (IRole r : cc.getGuild().getRoles()) {
            sb.append(String.format("%-33s%-18s%n", r.getName(), r.getStringID()));
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.withAuthorName(cc.getGuild().getName() + "'s Roles");
        embed.withAuthorIcon(cc.getGuild().getIconURL());
        embed.withColor(36, 153, 153);
        embed.setLenient(true);
        embed.withFooterIcon(cc.getAuthor().getAvatarURL());
        embed.withFooterText("Requested by " + cc.getAuthor().getName());
        embed.appendDescription(sb.append("```").toString());
        embed.withTimestamp(System.currentTimeMillis());
        cc.replyWith(embed.build());
    }
}
