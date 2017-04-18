package Techtony96.Discord.modules.audiostreamer;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.modules.audiostreamer.playlists.PlaylistManager;
import Techtony96.Discord.modules.audiostreamer.voice.VoiceManager;
import sx.blah.discord.modules.IModule;

import java.awt.*;

/**
 * Created by Tony on 4/14/2017.
 */
public class AudioStreamer extends CustomModule implements IModule {

    public static final String ADMIN_ROLE = "AudioStreamerAdmin";
    public static final Color EMBED_COLOR = new Color(255, 215, 0);

    private static PlaylistManager playlistManager;
    private static VoiceManager voiceManager;

    public AudioStreamer() {
        super("Audio Streamer", "0.1");
        playlistManager = new PlaylistManager();
        voiceManager = new VoiceManager();
    }

    public static PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public static VoiceManager getVoiceManager() {
        return voiceManager;
    }
}
