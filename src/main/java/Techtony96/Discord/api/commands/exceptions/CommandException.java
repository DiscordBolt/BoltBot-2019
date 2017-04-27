package Techtony96.Discord.api.commands.exceptions;

import Techtony96.Discord.utils.ExceptionMessage;

/**
 * Created by Tony on 4/19/2017.
 */
public class CommandException extends Exception {

    public CommandException() {
        super(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
    }

    public CommandException(String message) {
        super(message);
    }
}
