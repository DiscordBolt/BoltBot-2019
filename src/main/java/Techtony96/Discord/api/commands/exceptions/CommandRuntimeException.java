package Techtony96.Discord.api.commands.exceptions;

import Techtony96.Discord.utils.ExceptionMessage;

/**
 * Created by Tony on 4/19/2017.
 */
public class CommandRuntimeException extends RuntimeException {

    public CommandRuntimeException() {
        super(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
    }

    public CommandRuntimeException(String message) {
        super(message);
    }
}
