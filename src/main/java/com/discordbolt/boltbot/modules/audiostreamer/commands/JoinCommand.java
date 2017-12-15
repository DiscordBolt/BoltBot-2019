package com.discordbolt.boltbot.modules.audiostreamer.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/22/2017.
 */
public class JoinCommand {

    @BotCommand(command = "join", module = AudioStreamer.MODULE, description = "Have Discord.java join your voice channel", usage = "Join", args = 1, allowedChannels = "music")
    public static void joinCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getVoiceManager().joinChannel(cc.getGuild(), cc.getAuthor(), cc.getAuthor().getVoiceStateForGuild(cc.getGuild()).getChannel());
    }

    @BotCommand(command = "leave", module = AudioStreamer.MODULE, description = "Force Discord.java to leave the voice channel", usage = "Leave", args = 1, allowedChannels = "music")
    public static void leaveCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getVoiceManager().leaveChannel(cc.getGuild(), cc.getAuthor());
    }
}
