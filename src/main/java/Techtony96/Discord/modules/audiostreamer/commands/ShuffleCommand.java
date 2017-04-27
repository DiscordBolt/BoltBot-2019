package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/26/2017.
 */
public class ShuffleCommand {

    @BotCommand(command = "shuffle", description = "Shuffle the current queue", usage = "!Shuffle", module = "Audio Streamer Module")
    public static void shuffleCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().shuffle(cc.getGuild(), cc.getUser());
        } catch (CommandPermissionException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
