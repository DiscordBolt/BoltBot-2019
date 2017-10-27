package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/22/2017.
 */
public class JoinCommand {

    @BotCommand(command = "join", module = "Audio Streamer Module", description = "Have Discord.java join your voice channel", usage = "Join", args = 1, allowedChannels = "music")
    public static void joinCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().joinChannel(cc.getGuild(), cc.getAuthor(), cc.getAuthor().getVoiceStateForGuild(cc.getGuild()).getChannel());
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }

    @BotCommand(command = "leave", module = "Audio Streamer Module", description = "Force Discord.java to leave the voice channel", usage = "Leave", args = 1, allowedChannels = "music")
    public static void leaveCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().leaveChannel(cc.getGuild(), cc.getAuthor());
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
