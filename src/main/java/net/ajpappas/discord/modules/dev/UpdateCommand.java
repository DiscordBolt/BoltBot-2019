package net.ajpappas.discord.modules.dev;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.utils.ExceptionMessage;
import net.ajpappas.discord.utils.UserUtil;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Tony on 4/27/2017.
 */
public class UpdateCommand {

    @BotCommand(command = "update", description = "Update the bot and restart.", usage = "Update", module = "Development", permissions = Permissions.ADMINISTRATOR)
    public static void updateCommand(CommandContext cc) {
        if (!UserUtil.isBotOwner(cc.getAuthor())) {
            cc.replyWith(ExceptionMessage.PERMISSION_DENIED);
            return;
        }
        System.exit(0);
    }
}
