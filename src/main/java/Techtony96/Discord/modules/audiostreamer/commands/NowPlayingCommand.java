package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Created by Tony on 4/22/2017.
 */
public class NowPlayingCommand {

    @BotCommand(command = "nowplaying", aliases = {"np", "current", "playing"}, module = "Audio Streamer Module", description = "View what is currently playing", usage = "!NowPlaying")
    public static void nowPlayingCommand(CommandContext cc) {
        AudioTrack at = AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild());
        if (at != null) {
            cc.replyWith("Currently playing: " + at.getInfo().title + " by " + at.getInfo().author);
            return;
        } else {
            cc.replyWith("Nothing is currently playing. Play something with !Play");
            return;
        }
    }
}
