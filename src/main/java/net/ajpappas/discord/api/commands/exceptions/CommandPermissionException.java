package net.ajpappas.discord.api.commands.exceptions;

import net.ajpappas.discord.utils.ExceptionMessage;

/**
 * Created by Tony on 4/19/2017.
 */
public class CommandPermissionException extends CommandException {

    public CommandPermissionException() {
        super(ExceptionMessage.PERMISSION_DENIED);
    }

    public CommandPermissionException(String message) {
        super(message);
    }
}
