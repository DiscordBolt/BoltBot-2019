package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/22/2017.
 */
public class JoinCommand {

    @BotCommand(command = "join", module = "Audio Streamer Module", description = "Have Discord.java join your voice channel", usage = "Join", args = 1, allowedChannels = "music")
    public static void joinCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().joinChannel(cc.getGuild(), cc.getUser(), cc.getUser().getVoiceStateForGuild(cc.getGuild()).getChannel());
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }

    @BotCommand(command = "leave", module = "Audio Streamer Module", description = "Force Discord.java to leave the voice channel", usage = "Leave", args = 1, allowedChannels = "music")
    public static void leaveCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().leaveChannel(cc.getGuild(), cc.getUser());
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
