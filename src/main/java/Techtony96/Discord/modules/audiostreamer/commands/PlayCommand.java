package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;

/**
 * Created by Evan on 4/16/2017.
 */
public class PlayCommand {

    @BotCommand(command = "play", description = "Play a song", usage = "!Play URL", module = "Audio Streamer Module")
    public static void playCommand(CommandContext cc) {
        cc.replyWith("Loading your song");
        AudioStreamer.getVoiceManager().loadAndPlay(cc.getChannel(), cc.getArgument(1));
    }
}
