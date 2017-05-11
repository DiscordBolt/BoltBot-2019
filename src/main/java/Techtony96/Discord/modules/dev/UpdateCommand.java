package Techtony96.Discord.modules.dev;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.utils.ExceptionMessage;
import Techtony96.Discord.utils.Logger;
import Techtony96.Discord.utils.UserUtil;
import sx.blah.discord.handle.obj.Permissions;

import java.io.IOException;

/**
 * Created by Tony on 4/27/2017.
 */
public class UpdateCommand {

    @BotCommand(command = "update", description = "Update the bot and restart.", usage = "!Update", module = "dev", permissions = Permissions.ADMINISTRATOR, secret = true)
    public static void updateCommand(CommandContext cc) {
        if (!UserUtil.isBotOwner(cc.getUser())) {
            cc.replyWith(ExceptionMessage.PERMISSION_DENIED);
            return;
        }
        try {
            Process p = Runtime.getRuntime().exec("./update.sh");
            p.waitFor();
            System.exit(0);
        } catch (IOException e) {
            cc.replyWith(e.getMessage());
            Logger.debug(e);
        } catch (InterruptedException e) {
            cc.replyWith(e.getMessage());
            Logger.debug(e);
        }
    }
}
