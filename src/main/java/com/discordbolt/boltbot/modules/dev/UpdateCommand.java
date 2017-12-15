package com.discordbolt.boltbot.modules.dev;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.api.command.exceptions.CommandPermissionException;
import com.discordbolt.boltbot.utils.ExceptionMessage;
import com.discordbolt.boltbot.utils.UserUtil;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Tony on 4/27/2017.
 */
public class UpdateCommand {

    @BotCommand(command = "update", description = "Update the bot and restart.", usage = "Update", module = "Development", permissions = Permissions.ADMINISTRATOR)
    public static void updateCommand(CommandContext cc) throws CommandException {
        if (!UserUtil.isBotOwner(cc.getAuthor()))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);

        System.exit(0);
    }
}
