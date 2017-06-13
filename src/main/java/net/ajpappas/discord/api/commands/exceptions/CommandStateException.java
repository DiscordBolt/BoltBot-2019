package net.ajpappas.discord.api.commands.exceptions;

import net.ajpappas.discord.utils.ExceptionMessage;

/**
 * Created by Tony on 4/19/2017.
 */
public class CommandStateException extends CommandException {

    public CommandStateException() {
        super(ExceptionMessage.BAD_STATE);
    }

    public CommandStateException(String message) {
        super(message);
    }
}
