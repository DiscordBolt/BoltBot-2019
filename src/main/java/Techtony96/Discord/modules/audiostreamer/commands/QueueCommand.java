package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.voice.VoiceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.util.EmbedBuilder;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tony on 4/22/2017.
 */
public class QueueCommand {

    @BotCommand(command = "queue", module = "Audio Streamer Module", description = "Print out the list of currently queued songs.", usage = "Queue")
    public static void queueCommand(CommandContext cc) {
        List<AudioTrack> queue = AudioStreamer.getVoiceManager().getQueue(cc.getGuild());
        AudioTrack nowPlaying = AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild());

        if (cc.getArgCount() > 1 && cc.getArgument(1).equalsIgnoreCase("clear")) {
            try {
                AudioStreamer.getVoiceManager().clearQueue(cc.getGuild(), cc.getUser());
                return;
            } catch (CommandPermissionException e) {
                cc.replyWith(e.getMessage());
                return;
            }
        }


            if (nowPlaying == null) {
                cc.replyWith("The queue is empty! Play something with !Play");
                return;
            }

            final AudioTrack currentTrack = AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild());
            final long totalTime = AudioStreamer.getVoiceManager().getQueue(cc.getGuild()).stream()
                    .map(AudioTrack::getDuration)
                    .reduce(0L, (x, y) -> x + y)
                    + (currentTrack != null ? currentTrack.getDuration() : 0);

            EmbedBuilder embed = new EmbedBuilder();

            embed.withTitle(":clock1030: Queue Length");
            embed.withDescription(getFormattedTime(totalTime));

            embed.withColor(AudioStreamer.EMBED_COLOR);
            StringBuilder songs = new StringBuilder();

            int i = 2;
            songs.append("***1. " + nowPlaying.getInfo().title + "***").append('\n');

            for (AudioTrack audioTrack : queue) {
                if ((songs.length() + audioTrack.getInfo().title.length()) >= 975 && (i - 1) < queue.size()) {
                    songs.append("\n");
                    songs.append("    ***... and " + (queue.size() - (i - 1)) + " more***");
                    break;
                }
                songs.append(i++).append(". ").append(audioTrack.getInfo().title).append('\n');
            }

            embed.appendField(":arrow_forward: Now Playing", songs.length() > 1 ? songs.toString() : "\n", true);
            cc.replyWith(embed.build());

    }

    public static String getFormattedTime(long timestamp){

        final long totalSeconds = timestamp / 1000;
        final String[] strings = new String[] {"days", "hours", "minutes", "seconds"};
        final long[] data = new long[4];
        data[0] = TimeUnit.SECONDS.toDays(totalSeconds);
        data[1] = TimeUnit.SECONDS.toHours(totalSeconds) - (data[0] * 24);
        data[2] = TimeUnit.SECONDS.toMinutes(totalSeconds) - (TimeUnit.SECONDS.toHours(totalSeconds) * 60);
        data[3] = TimeUnit.SECONDS.toSeconds(totalSeconds) - (TimeUnit.SECONDS.toMinutes(totalSeconds) * 60);

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < data.length; i++) {

            long time = data[i];

            if (time > 0) {
                stringBuilder.append(time + " " + (time == 1 ? strings[i].substring(0, strings[i].length() - 1) : strings[i]));

                if (i != data.length - 1) {
                    if (!(i + 2 > (data.length - 1))) {
                        if (data[i + 2] <= 0) {
                            stringBuilder.append(" and ");
                        } else {
                            stringBuilder.append(", ");
                        }
                    } else {
                        if (i == data.length - 2) {
                            stringBuilder.append(" and ");
                        } else {
                            stringBuilder.append(", ");
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();

    }

}
