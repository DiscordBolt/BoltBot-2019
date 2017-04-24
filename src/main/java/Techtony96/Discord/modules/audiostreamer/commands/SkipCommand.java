package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/22/2017.
 */
public class SkipCommand {

    @BotCommand(command = "skip", module = "Audio Streamer Module", description = "Skip to the next song.", usage = "!Skip <Force>", minArgs = 1, maxArgs = 2)
    public static void leaveCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().skip(cc.getGuild(), cc.getUser(), cc.getArgCount() > 1);
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
