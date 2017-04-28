package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

/**
 * Created by Tony on 4/22/2017.
 */
public class QueueCommand {

    @BotCommand(command = "queue", module = "Audio Streamer Module", description = "Print out the list of currently queued songs.", usage = "!Queue")
    public static void queueCommand(CommandContext cc) {
        List<AudioTrack> queue = AudioStreamer.getVoiceManager().getQueue(cc.getGuild());
        AudioTrack nowPlaying = AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild());

        if (cc.getArgCount() > 1 && cc.getArgument(1).equalsIgnoreCase("clear")) {
            try {
                AudioStreamer.getVoiceManager().clearQueue(cc.getGuild(), cc.getUser());
            } catch (CommandPermissionException e) {
                cc.replyWith(e.getMessage());
                return;
            }
        }


        if (nowPlaying == null) {
            cc.replyWith("The queue is empty! Play something with !Play");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.withTitle(":arrow_forward: Now Playing");
        embed.withColor(AudioStreamer.EMBED_COLOR);
        StringBuilder songs = new StringBuilder();
        int i = 2;
        songs.append("1. " + nowPlaying.getInfo().title).append('\n');
        for (AudioTrack audioTrack : queue) {
            if (songs.length() >= 1900)
                break;
            songs.append(i++).append(". ").append(audioTrack.getInfo().title).append('\n');
        }
        if (songs.length() > 2048)
            songs.setLength(2048);
        embed.withDescription(songs.length() > 1 ? songs.toString() : "\n");
        cc.replyWith(embed.build());
    }
}
