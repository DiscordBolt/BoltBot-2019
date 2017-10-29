package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.modules.audiostreamer.playlists.Playlist;

/**
 * Created by Evan on 4/16/2017.
 */
public class PlayCommand {

    @BotCommand(command = "play", module = AudioStreamer.MODULE, description = "Instruct the bot to start playing something.", usage = "Play help", allowedChannels = "music", minArgs = 1, maxArgs = 2)
    public static void playCommand(CommandContext cc) {
        if (cc.getArgCount() == 1) {
            if (AudioStreamer.getVoiceManager().isPaused(cc.getGuild()))
                try {
                    AudioStreamer.getVoiceManager().unpause(cc.getGuild(), cc.getAuthor());
                } catch (CommandException e) {
                    cc.replyWith(e.getMessage());
                }
            else
                cc.replyWith("I am not paused!");
            return;
        }

        try {
            AudioStreamer.getVoiceManager().queue(cc.getGuild(), cc.getAuthor(), cc.getArgument(1));
            cc.replyWith("Your song is now being queued up");
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
        }
    }

    @BotCommand(command = {"play", "-p"}, module = AudioStreamer.MODULE, description = "Play a saved playlist.", usage = "Play playlist <name>", allowedChannels = "music", minArgs = 2, maxArgs = 100)
    public static void playPlaylist(CommandContext cc) {

        if (cc.getArgCount() == 2) {
            Playlist current = AudioStreamer.getPlaylistManager().getSelectedPlaylist(cc.getAuthor().getLongID());
            if (current == null) {
                cc.replyWith("You do not have a selected playlist to queue!");
                return;
            }
            queuePlaylist(cc, current);
            return;
        } else {
            String playlistRequest = cc.combineArgs(2, cc.getArgCount() - 1);
            if (playlistRequest.contains(":")) {
                String playlistTitle = playlistRequest.split(":")[0];
                int songNumber;
                try {
                    songNumber = Integer.valueOf(playlistRequest.split(":")[1]);
                } catch (NumberFormatException e) {
                    cc.replyWith("\"" + playlistRequest.split(":")[1] + "\" is not a valid number!");
                    return;
                }

                Playlist toPlay = AudioStreamer.getPlaylistManager().getPlaylist(playlistTitle).orElse(null);
                if (toPlay == null) {
                    cc.replyWith("\"" + playlistTitle + "\" could not be found!");
                    return;
                }
                if (songNumber < 1 || songNumber > toPlay.getSongIDs().size()) {
                    cc.replyWith("\"" + songNumber + "\" is not a valid number for \"" + toPlay.getTitle() + "\"");
                    return;
                }
                try {
                    AudioStreamer.getVoiceManager().queue(cc.getGuild(), cc.getAuthor(), toPlay.getSongIDs().get(songNumber - 1));
                } catch (CommandException e) {
                    cc.replyWith(e.getMessage());
                    return;
                }
            } else {
                Playlist toPlay = AudioStreamer.getPlaylistManager().getPlaylist(playlistRequest).orElse(null);
                if (toPlay == null) {
                    cc.replyWith("\"" + playlistRequest + "\" could not be found!");
                    return;
                }
                queuePlaylist(cc, toPlay);
            }
        }
    }

    @BotCommand(command = {"play", "random"}, module = AudioStreamer.MODULE, description = "Play a random song.", usage = "Play random", allowedChannels = "music", args = 2)
    public static void playRandom(CommandContext cc) {
        AudioStreamer.getVoiceManager().playRandom(cc.getGuild(), cc.getAuthor());
        if (AudioStreamer.getVoiceManager().isPlayingRandom(cc.getGuild())) {
            cc.replyWith("I will now continuously queue up random songs!");
        } else {
            cc.replyWith("I will stop playing random songs.");
        }
    }

    private static void queuePlaylist(CommandContext cc, Playlist current) {
        try {
            AudioStreamer.getVoiceManager().queue(cc.getGuild(), cc.getAuthor(), current);
            cc.replyWith("Your playlist is now being queued and may take ~30 seconds to fully appear in the queue.");
            return;
        } catch (CommandException e) {
            cc.replyWith(e.getMessage());
            return;
        }
    }
}
