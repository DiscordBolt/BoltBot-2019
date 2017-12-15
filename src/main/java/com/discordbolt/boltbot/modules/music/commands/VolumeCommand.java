package com.discordbolt.boltbot.modules.music.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.modules.music.MusicModule;

/**
 * Created by Tony on 4/22/2017.
 */
public class VolumeCommand {

    @BotCommand(command = "volume", module = MusicModule.MODULE, description = "Set the music volume", usage = "Volume", args = 1, allowedChannels = "music")
    public static void volumeCommand(CommandContext cc) {
        cc.replyWith("The volume is set to " + MusicModule.getVoiceManager().getVolume(cc.getGuild()));
    }

    @BotCommand(command = {"volume", "set"}, module = MusicModule.MODULE, description = "Set the music volume", usage = "Volume set [0-150]", args = 3, allowedChannels = "music")
    public static void joinCommand(CommandContext cc) throws CommandException {
        if (!cc.getArgument(2).matches("^-?\\d+$"))
            throw new CommandArgumentException("\"" + cc.getArgument(2) + "\" is not a valid number.");

        MusicModule.getVoiceManager().setVolume(cc.getGuild(), cc.getAuthor(), Integer.valueOf(cc.getArgument(2)));
    }
}
