package com.discordbolt.boltbot.modules.music.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.modules.music.MusicModule;

/**
 * Created by Tony on 4/22/2017.
 */
public class JoinCommand {

    @BotCommand(command = "join", module = MusicModule.MODULE, description = "Have the bot join your voice channel", usage = "Join", args = 1, allowedChannels = "music")
    public static void joinCommand(CommandContext cc) throws CommandException {
        MusicModule.getVoiceManager().joinChannel(cc.getGuild(), cc.getAuthor(), cc.getAuthor().getVoiceStateForGuild(cc.getGuild()).getChannel());
    }

    @BotCommand(command = "leave", module = MusicModule.MODULE, description = "Force the bot to leave the voice channel", usage = "Leave", args = 1, allowedChannels = "music")
    public static void leaveCommand(CommandContext cc) throws CommandException {
        MusicModule.getVoiceManager().leaveChannel(cc.getGuild(), cc.getAuthor());
    }
}
