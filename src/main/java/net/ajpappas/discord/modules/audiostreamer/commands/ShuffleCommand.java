package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/26/2017.
 */
public class ShuffleCommand {

    @BotCommand(command = "shuffle", description = "Shuffle the current queue", usage = "Shuffle", module = AudioStreamer.MODULE, allowedChannels = "music")
    public static void shuffleCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getVoiceManager().shuffle(cc.getGuild(), cc.getAuthor());
    }
}
