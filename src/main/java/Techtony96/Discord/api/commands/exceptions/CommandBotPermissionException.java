package Techtony96.Discord.api.commands.exceptions;

import Techtony96.Discord.utils.ExceptionMessage;

/**
 * Created by Tony on 4/19/2017.
 */
public class CommandBotPermissionException extends CommandException {

    public CommandBotPermissionException() {
        super(ExceptionMessage.BOT_PERMISSION_DENIED);
    }

    public CommandBotPermissionException(String message) {
        super(message);
    }
}
