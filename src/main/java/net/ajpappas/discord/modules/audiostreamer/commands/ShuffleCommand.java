package net.ajpappas.discord.modules.audiostreamer.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
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
