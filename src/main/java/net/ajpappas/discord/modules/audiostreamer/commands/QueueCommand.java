package net.ajpappas.discord.modules.audiostreamer.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandArgumentException;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.api.commands.exceptions.CommandStateException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.utils.TimeUtil;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

/**
 * Created by Tony on 4/22/2017.
 */
public class QueueCommand {

    @BotCommand(command = "queue", module = AudioStreamer.MODULE, description = "Show the currently queued songs", usage = "Queue", allowedChannels = "music", args = 1)
    public static void queueCommand(CommandContext cc) throws CommandException {
        List<AudioTrack> queue = AudioStreamer.getVoiceManager().getQueue(cc.getGuild());
        AudioTrack nowPlaying = AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild());
        if (nowPlaying == null)
            throw new CommandStateException("The queue is empty! Play something with !Play");

        final long totalTime = AudioStreamer.getVoiceManager().getQueue(cc.getGuild()).stream().map(AudioTrack::getDuration).reduce(0L, (x, y) -> x + y) + AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild()).getDuration();

        EmbedBuilder embed = new EmbedBuilder();
        embed.withTitle(":clock1030: Queue Length");
        embed.withDescription(TimeUtil.getFormattedTime(totalTime));
        embed.withColor(AudioStreamer.EMBED_COLOR);

        StringBuilder songs = new StringBuilder();
        int i = 2;
        songs.append("***1. ").append(nowPlaying.getInfo().title).append("***").append('\n');

        for (AudioTrack audioTrack : queue) {
            if ((songs.length() + audioTrack.getInfo().title.length()) >= 975 && (i - 1) < queue.size()) {
                songs.append("\n");
                songs.append("    ***... and ").append(queue.size() - (i - 1)).append(" more***");
                break;
            }
            songs.append(i++).append(". ").append(audioTrack.getInfo().title).append('\n');
        }

        embed.appendField(":arrow_forward: Now Playing", songs.length() > 1 ? songs.toString() : "\n", true);
        cc.replyWith(embed.build());
    }

    @BotCommand(command = {"queue", "clear"}, module = AudioStreamer.MODULE, description = "Remove all songs from the queue", usage = "Queue clear", allowedChannels = "music", args = 2)
    public static void queueClearCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getVoiceManager().clearQueue(cc.getGuild(), cc.getAuthor());
    }

    @BotCommand(command = {"queue", "remove"}, module = AudioStreamer.MODULE, description = "Remove a song from the queue", usage = "Queue remove [number]", allowedChannels = "music", args = 3)
    public static void queueRemoveCommand(CommandContext cc) throws CommandException {
        if (!cc.getArgument(2).matches("^-?\\d+$"))
            throw new CommandArgumentException("\"" + cc.getArgument(2) + "\" is not a valid song number.");
        int songIndex = Integer.parseInt(cc.getArgument(2));
        if (songIndex < 1 || songIndex > AudioStreamer.getVoiceManager().getQueue(cc.getGuild()).size() + 1)
            throw new CommandArgumentException("\"" + cc.getArgument(2) + "\" is not a valid song number.");

        if (songIndex == 1) { //just skip
            AudioStreamer.getVoiceManager().skip(cc.getGuild(), cc.getAuthor(), true, 1);
            cc.replyWith("Song #1: \"" + AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild()).getInfo().title + "\" has been removed from the queue.");
        } else {
            AudioStreamer.getVoiceManager().dequeue(cc.getGuild(), cc.getAuthor(), AudioStreamer.getVoiceManager().getQueue(cc.getGuild()).get(songIndex - 2).getInfo().identifier);
            cc.replyWith("Song #" + (songIndex) + ": \"" + AudioStreamer.getVoiceManager().getQueue(cc.getGuild()).get(songIndex - 2).getInfo().title + "\" has been removed from the queue.");
        }
    }
}