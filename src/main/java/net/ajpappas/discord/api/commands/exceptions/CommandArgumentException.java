package net.ajpappas.discord.api.commands.exceptions;

import net.ajpappas.discord.utils.ExceptionMessage;

/**
 * Created by Tony on 4/19/2017.
 */
public class CommandArgumentException extends CommandException {

    public CommandArgumentException() {
        super(ExceptionMessage.INCORRECT_USAGE);
    }

    public CommandArgumentException(String message) {
        super(message);
    }
}
