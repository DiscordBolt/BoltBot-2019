package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandRuntimeException;
import Techtony96.Discord.api.commands.exceptions.CommandStateException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.playlists.Playlist;
import Techtony96.Discord.modules.audiostreamer.playlists.PlaylistManager;
import Techtony96.Discord.modules.audiostreamer.voice.VoiceManager;
import Techtony96.Discord.utils.ExceptionMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Evan on 4/16/2017.
 */
public class PlayCommand {

    private static final String PLAY_SONG = "!Play [URL]";
    private static final String PLAY_PLAYLIST = "!Play playlist <playlist name>";
    private static final String PLAY_RANDOM = "!Play random";

    @BotCommand(command = "play", module = "Audio Streamer Module", description = "Instruct the bot to start playing something.", usage = "View !play help")
    public static void playCommand(CommandContext cc) {
        if (cc.getArgCount() < 2) {
            cc.replyWith(ExceptionMessage.INCORRECT_USAGE);
            cc.sendUsage();
            return;
        }

        PlaylistManager playlistManager = AudioStreamer.getPlaylistManager();
        VoiceManager voiceManager = AudioStreamer.getVoiceManager();
        Playlist current = playlistManager.getSelectedPlaylist(cc.getUser().getStringID());
        String instruction = cc.getArgument(1);

        if (instruction.equalsIgnoreCase("playlist")) {
            Playlist toPlay = null;
            if (cc.getArgCount() == 2) {
                toPlay = current;
            } else if (cc.getArgCount() > 2) {
                toPlay = playlistManager.getPlaylist(cc.combineArgs(2, cc.getArgCount() - 1)).orElse(null);
            }
            try {
                voiceManager.queue(cc.getGuild(), cc.getUser(), toPlay);
            } catch (CommandStateException e) {
                cc.replyWith(e.getMessage());
                return;
            }
        } else if (instruction.equalsIgnoreCase("random")) {
            String songID = getRandomSong();
            try {
                String songTitle = voiceManager.queue(cc.getGuild(), cc.getUser(), songID);
                cc.replyWith("Added \"" + songTitle + "\" to the queue.");
            } catch (CommandRuntimeException e) {
                cc.replyWith(e.getMessage());
            }
            return;
        } else {
            try {
                String songTitle = voiceManager.queue(cc.getGuild(), cc.getUser(), cc.getArgument(1));
                cc.replyWith("Added \"" + songTitle + "\" to the queue.");
            } catch (CommandRuntimeException e) {
                cc.replyWith(e.getMessage());
            }
            return;
        }
    }

    private static String getRandomSong() {
        List<Playlist> playlists = new ArrayList<>(AudioStreamer.getPlaylistManager().getPlaylists());
        Playlist pl = playlists.get(new Random().nextInt(playlists.size()));
        List<String> songs = pl.getSongIDs();
        return songs.get(new Random().nextInt(songs.size()));
    }
}
