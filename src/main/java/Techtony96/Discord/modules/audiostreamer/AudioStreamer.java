package Techtony96.Discord.modules.audiostreamer;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.modules.audiostreamer.playlists.PlaylistManager;
import Techtony96.Discord.modules.audiostreamer.voice.VoiceManager;
import Techtony96.Discord.utils.UserUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

import java.awt.*;

/**
 * Created by Tony on 4/14/2017.
 */
public class AudioStreamer extends CustomModule implements IModule {

    public static final String ADMIN_ROLE = "AudioStreamerAdmin";
    public static final String DJ_ROLE = "AudioStreamerDJ";
    public static final String TEXT_CHANNEL = "music";
    public static final double VOTE_SKIP_PERCENT = 0.40;
    public static final Color EMBED_COLOR = new Color(255, 215, 0);

    private static PlaylistManager playlistManager;
    private static VoiceManager voiceManager;

    public AudioStreamer() {
        super("Audio Streamer Module", "0.1");
        playlistManager = new PlaylistManager();
        voiceManager = new VoiceManager();
    }

    public static PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public static VoiceManager getVoiceManager() {
        return voiceManager;
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        client.getDispatcher().registerListener(voiceManager);
    }

    public static boolean hasAdminPermissions(IUser user, IGuild guild) {
        return UserUtil.hasRole(user, guild, ADMIN_ROLE);
    }

    public static boolean hasDJPermissions(IUser user, IGuild guild) {
        return UserUtil.hasRole(user, guild, DJ_ROLE) || hasAdminPermissions(user, guild);
    }
}
