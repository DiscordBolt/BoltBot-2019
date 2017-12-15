package net.ajpappas.discord.modules.audiostreamer.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Techt on 4/25/2017.
 */
public class PauseCommand {

    @BotCommand(command = "pause", description = "Pause the currently playing song", usage = "Pause", module = AudioStreamer.MODULE, allowedChannels = "music")
    public static void pauseCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getVoiceManager().pause(cc.getGuild(), cc.getAuthor());
    }

    @BotCommand(command = "unpause", aliases = "resume", description = "Unpause the currently playing song", usage = "Unpause", module = AudioStreamer.MODULE, allowedChannels = "music")
    public static void unpauseCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getVoiceManager().unpause(cc.getGuild(), cc.getAuthor());
    }
}
