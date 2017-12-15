package com.discordbolt.boltbot.modules.music.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.modules.music.MusicModule;

/**
 * Created by Techt on 4/25/2017.
 */
public class PauseCommand {

    @BotCommand(command = "pause", description = "Pause the currently playing song", usage = "Pause", module = MusicModule.MODULE, allowedChannels = "music")
    public static void pauseCommand(CommandContext cc) throws CommandException {
        MusicModule.getVoiceManager().pause(cc.getGuild(), cc.getAuthor());
    }

    @BotCommand(command = "unpause", aliases = "resume", description = "Unpause the currently playing song", usage = "Unpause", module = MusicModule.MODULE, allowedChannels = "music")
    public static void unpauseCommand(CommandContext cc) throws CommandException {
        MusicModule.getVoiceManager().unpause(cc.getGuild(), cc.getAuthor());
    }
}
