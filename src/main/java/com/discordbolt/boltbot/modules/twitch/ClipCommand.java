package com.discordbolt.boltbot.modules.twitch;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.utils.ExceptionMessage;
import com.discordbolt.boltbot.utils.Logger;

import java.io.IOException;

public class ClipCommand {

    @BotCommand(command = "clip", aliases = "highlight", description = "Create a clip of a twitch broadcast", usage = "!Clip [channel]", module = "Twitch Module")
    public static void clipCommand(CommandContext cc) throws CommandException {
        String username;
        if (cc.getArgCount() >= 2) {
            username = cc.getArgument(1);
        } else if (cc.getAuthor().getPresence().getStreamingUrl().isPresent()) {
            username = cc.getAuthor().getPresence().getStreamingUrl().get().substring(cc.getAuthor().getPresence().getStreamingUrl().get().lastIndexOf("/") + 1);
        } else {
            throw new CommandArgumentException("You must provide a Twitch channel to clip!");
        }

        try {
            cc.replyWith("<" + TwitchModule.getTwitchAPI().getClip().generateClip(username) + ">");
        } catch (IllegalArgumentException e) {
            cc.replyWith(e.getMessage());
        } catch (IOException e) {
            cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
            Logger.error(e.getMessage());
            Logger.debug(e);
        }
    }
}
