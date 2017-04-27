package Techtony96.Discord.api.commands.exceptions;

import Techtony96.Discord.utils.ExceptionMessage;

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
