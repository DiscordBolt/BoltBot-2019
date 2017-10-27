package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandPermissionException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/26/2017.
 */
public class ShuffleCommand {

    @BotCommand(command = "shuffle", description = "Shuffle the current queue", usage = "Shuffle", module = "Audio Streamer Module", allowedChannels = "music")
    public static void shuffleCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().shuffle(cc.getGuild(), cc.getAuthor());
        } catch (CommandPermissionException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
