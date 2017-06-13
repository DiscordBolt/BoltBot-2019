package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Techt on 4/25/2017.
 */
public class PauseCommand {

    @BotCommand(command = "pause", description = "Pause the currently playing song", usage = "Pause", module = "Audio Streamer Module", allowedChannels = "music")
    public static void pauseCommand(CommandContext cc){
        try {
            AudioStreamer.getVoiceManager().pause(cc.getGuild(), cc.getUser());
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }

    @BotCommand(command = "unpause", aliases = "resume", description = "Unpause the currently playing song", usage = "Unpause", module = "Audio Streamer Module", allowedChannels = "music")
    public static void unpauseCommand(CommandContext cc){
        try {
            AudioStreamer.getVoiceManager().unpause(cc.getGuild(), cc.getUser());
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
