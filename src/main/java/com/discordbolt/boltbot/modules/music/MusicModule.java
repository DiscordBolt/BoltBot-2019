package com.discordbolt.boltbot.modules.music;

import com.discordbolt.boltbot.system.CustomModule;
import com.discordbolt.boltbot.modules.music.playlists.Playlist;
import com.discordbolt.boltbot.modules.music.playlists.PlaylistManager;
import com.discordbolt.boltbot.modules.music.voice.VoiceManager;
import com.discordbolt.boltbot.utils.UserUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Tony on 4/14/2017.
 */
public class MusicModule extends CustomModule implements IModule {

    public static final String ADMIN_ROLE = "AudioStreamerAdmin";
    public static final String DJ_ROLE = "AudioStreamerDJ";
    public static final String TEXT_CHANNEL = "music";
    public static final double VOTE_SKIP_PERCENT = 0.40;
    public static final Color EMBED_COLOR = new Color(255, 215, 0);
    public static final String MODULE = "Music Bot";

    private static PlaylistManager playlistManager;
    private static VoiceManager voiceManager;

    public MusicModule(IDiscordClient client) {
        super(client, "Audio Streamer Module", "0.1", "2.8.2", "Techtony96, Spikex21, and Jessee");
        playlistManager = new PlaylistManager();
        voiceManager = new VoiceManager();
        client.getDispatcher().registerListener(getVoiceManager());
    }

    public static PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public static VoiceManager getVoiceManager() {
        return voiceManager;
    }

    public static String getRandomSong() {
        ArrayList<Playlist> playlists = new ArrayList<>(MusicModule.getPlaylistManager().getPlaylists());
        Collections.shuffle(playlists);
        ArrayList<String> songs = new ArrayList<>(playlists.get(0).getSongIDs());
        Collections.shuffle(songs);
        return songs.get(0);
    }

    public static boolean hasAdminPermissions(IUser user, IGuild guild) {
        return UserUtil.hasRole(user, guild, ADMIN_ROLE);
    }

    public static boolean hasDJPermissions(IUser user, IGuild guild) {
        return UserUtil.hasRole(user, guild, DJ_ROLE) || hasAdminPermissions(user, guild);
    }

    public static EmbedObject createPlayingEmbed(IGuild guild, AudioTrack track) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(MusicModule.EMBED_COLOR);
        embed.withTitle(":musical_note: " + track.getInfo().title);
        //embed.withAuthorUrl(track.getInfo().uri);
        embed.withDescription(getVoiceManager().getTrackRequester(guild, track) != null ? "Requested by: " + getVoiceManager().getTrackRequester(guild, track) : "Unknown Requester");
        if (track.getInfo().uri.toLowerCase().contains("youtu")) {
            embed.withThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/0.jpg");
        }

        return embed.build();
    }
}
