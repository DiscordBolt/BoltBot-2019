package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Techt on 4/25/2017.
 */
public class PauseCommand {

    @BotCommand(command = "pause", description = "Pause the currently playing song", usage = "!Pause", module = "Audio Streamer Module")
    public static void pauseCommand(CommandContext cc){
        try {
            AudioStreamer.getVoiceManager().pause(cc.getGuild(), cc.getUser());
        } catch (CommandPermissionException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }

    @BotCommand(command = "unpause", description = "Unpause the currently playing song", usage = "!Unpause", module = "Audio Streamer Module")
    public static void unpauseCommand(CommandContext cc){
        try {
            AudioStreamer.getVoiceManager().unpause(cc.getGuild(), cc.getUser());
        } catch (CommandPermissionException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
