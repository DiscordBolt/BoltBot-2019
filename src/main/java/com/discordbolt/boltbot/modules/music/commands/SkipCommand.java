package com.discordbolt.boltbot.modules.music.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.modules.music.MusicModule;

/**
 * Created by Tony on 4/22/2017.
 */
public class SkipCommand {

    @BotCommand(command = "skip", module = MusicModule.MODULE, description = "Vote to skip the current song", usage = "Skip", args = 1, allowedChannels = "music")
    public static void skipCommand(CommandContext cc) throws CommandException {
        MusicModule.getVoiceManager().skip(cc.getGuild(), cc.getAuthor(), false, 1);
        cc.replyWith(cc.getAuthorDisplayName() + " has voted to skip this track.");
    }

    @BotCommand(command = {"skip", "force"}, module = MusicModule.MODULE, description = "Force skip a number of songs", usage = "Skip force {number}", minArgs = 2, maxArgs = 3, allowedChannels = "music")
    public static void skipForceCommand(CommandContext cc) throws CommandException {
        if (cc.getArgCount() == 2) {
            MusicModule.getVoiceManager().skip(cc.getGuild(), cc.getAuthor(), true, 1);
        } else {
            if (!cc.getArgument(2).matches("^-?\\d+$"))
                throw new CommandArgumentException("\"" + cc.getArgument(2) + "\" is not a valid number.");

            MusicModule.getVoiceManager().skip(cc.getGuild(), cc.getAuthor(), true, Integer.valueOf(cc.getArgument(2)));
        }
    }
}
