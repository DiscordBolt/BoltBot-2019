package com.discordbolt.boltbot.modules.music.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.api.command.exceptions.CommandStateException;
import com.discordbolt.boltbot.modules.music.MusicModule;
import com.discordbolt.boltbot.modules.music.playlists.Playlist;

/**
 * Created by Evan on 4/16/2017.
 * Rewritten by Tony on 10/29/2017
 */
public class PlayCommand {

    @BotCommand(command = "play", module = MusicModule.MODULE, description = "Queue up the requested song", usage = "Play {URL}", allowedChannels = "music", minArgs = 1, maxArgs = 2)
    public static void playCommand(CommandContext cc) throws CommandException {
        if (cc.getArgCount() == 1) {
            if (!MusicModule.getVoiceManager().isPaused(cc.getGuild()))
                throw new CommandStateException("I am not paused!");
            MusicModule.getVoiceManager().unpause(cc.getGuild(), cc.getAuthor());
        } else {
            MusicModule.getVoiceManager().queue(cc.getGuild(), cc.getAuthor(), cc.getArgument(1));
            cc.replyWith("Your song is now being queued up");
        }
    }

    @BotCommand(command = {"play", "-p"}, module = MusicModule.MODULE, description = "Queue up the requested playlist", usage = "Play playlist {number/name}", allowedChannels = "music", minArgs = 2, maxArgs = 100)
    public static void playPlaylist(CommandContext cc) throws CommandException {
        if (cc.getArgCount() == 2) {
            Playlist current = MusicModule.getPlaylistManager().getSelectedPlaylist(cc.getAuthor().getLongID());
            if (current == null)
                throw new CommandStateException("You do not have a selected playlist to queue!");

            MusicModule.getVoiceManager().queue(cc.getGuild(), cc.getAuthor(), current);
            cc.replyWith("Your playlist is now being queued and may take ~30 seconds to fully appear in the queue.");
        } else {
            String playlistRequest = cc.combineArgs(2, cc.getArgCount() - 1);
            if (playlistRequest.contains(":")) {
                String playlistTitle = playlistRequest.split(":")[0];
                String playlistNumber = playlistRequest.split(":")[1];
                int songNumber;
                try {
                    songNumber = Integer.valueOf(playlistNumber);
                } catch (NumberFormatException e) {
                    throw new CommandArgumentException("\"" + playlistRequest.split(":")[1] + "\" is not a valid number!");
                }

                Playlist toPlay = MusicModule.getPlaylistManager().getPlaylist(playlistTitle).orElse(null);
                if (toPlay == null)
                    throw new CommandArgumentException("\"" + playlistTitle + "\" could not be found!");
                if (songNumber < 1 || songNumber > toPlay.getSongIDs().size())
                    throw new CommandArgumentException("\"" + songNumber + "\" is not a valid number for \"" + toPlay.getTitle() + "\"");

                MusicModule.getVoiceManager().queue(cc.getGuild(), cc.getAuthor(), toPlay.getSongIDs().get(songNumber - 1));
            } else {
                Playlist toPlay = MusicModule.getPlaylistManager().getPlaylist(playlistRequest).orElse(null);
                if (toPlay == null)
                    throw new CommandArgumentException("\"" + playlistRequest + "\" could not be found!");

                MusicModule.getVoiceManager().queue(cc.getGuild(), cc.getAuthor(), toPlay);
                cc.replyWith("Your playlist is now being queued and may take ~30 seconds to fully appear in the queue.");
            }
        }
    }

    @BotCommand(command = {"play", "random"}, module = MusicModule.MODULE, description = "Start/stop playing random songs", usage = "Play random", allowedChannels = "music", args = 2)
    public static void playRandom(CommandContext cc) {
        MusicModule.getVoiceManager().playRandom(cc.getGuild(), cc.getAuthor());
        if (MusicModule.getVoiceManager().isPlayingRandom(cc.getGuild())) {
            cc.replyWith("I will now continuously queue up random songs!");
        } else {
            cc.replyWith("I will stop playing random songs.");
        }
    }
}
