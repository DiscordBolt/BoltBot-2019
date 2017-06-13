package net.ajpappas.discord.modules.tempchannels.exceptions;

public class DuplicateChannelException extends Exception {

    public DuplicateChannelException() {
        super("Tried to create a channel for a user that already has a private channel.");
    }

    public DuplicateChannelException(String message) {
        super(message);
    }
}
