package Techtony96.Discord.modules.audiostreamer;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.modules.audiostreamer.playlists.PlaylistManager;
import Techtony96.Discord.modules.audiostreamer.songs.SongManager;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

import java.awt.*;

/**
 * Created by Tony on 4/14/2017.
 */
public class AudioStreamer extends CustomModule implements IModule {

    private static PlaylistManager playlistManager;
    private static SongManager songManager;

    public static final String ADMIN_ROLE = "AudioStreamerAdmin";
    public static final Color EMBED_COLOR = new Color(255, 215, 0);

    public AudioStreamer() {
        super("Audio Streamer", "0.1");
        songManager = new SongManager();
        playlistManager = new PlaylistManager();
    }

    public static PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public static SongManager getSongManager() {
        return songManager;
    }
}
