package com.discordbolt.boltbot.modules.audiostreamer.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/22/2017.
 */
public class SkipCommand {

    @BotCommand(command = "skip", module = AudioStreamer.MODULE, description = "Vote to skip the current song", usage = "Skip", args = 1, allowedChannels = "music")
    public static void skipCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getVoiceManager().skip(cc.getGuild(), cc.getAuthor(), false, 1);
    }

    @BotCommand(command = {"skip", "force"}, module = AudioStreamer.MODULE, description = "Force skip a number of songs", usage = "Skip force {number}", minArgs = 2, maxArgs = 3, allowedChannels = "music")
    public static void skipForceCommand(CommandContext cc) throws CommandException {
        if (cc.getArgCount() == 2) {
            AudioStreamer.getVoiceManager().skip(cc.getGuild(), cc.getAuthor(), true, 1);
        } else {
            if (!cc.getArgument(2).matches("^-?\\d+$"))
                throw new CommandArgumentException("\"" + cc.getArgument(2) + "\" is not a valid number.");

            AudioStreamer.getVoiceManager().skip(cc.getGuild(), cc.getAuthor(), true, Integer.valueOf(cc.getArgument(2)));
        }
    }
}
