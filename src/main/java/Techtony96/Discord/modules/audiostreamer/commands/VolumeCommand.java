package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Tony on 4/22/2017.
 */
public class VolumeCommand {

    @BotCommand(command = "volume", module = "Audio Streamer Module", description = "Change the music volume", usage = "Volume [0-150]", args = 2)
    public static void joinCommand(CommandContext cc) {
        try {
            AudioStreamer.getVoiceManager().setVolume(cc.getGuild(), cc.getUser(), Integer.valueOf(cc.getArgument(1)));
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        } catch (NumberFormatException e) {
            cc.replyWith(cc.getArgument(1) + " is not a valid number!");
            return;
        }
    }
}
