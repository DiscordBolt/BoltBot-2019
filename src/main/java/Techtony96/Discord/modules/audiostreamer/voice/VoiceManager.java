package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.Logger;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.HashMap;

/**
 * Created by Tony on 4/16/2017.
 */
public class VoiceManager {

    private HashMap<Long, DJ> musicManagers = new HashMap<>();
    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public VoiceManager() {
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        config();
    }

    private void config() {
        //todo
        playerManager.getConfiguration();
    }

    private synchronized DJ getGuildAudioPlayer(IGuild guild) {
        DJ musicManager = musicManagers.get(guild.getLongID());

        if (musicManager == null) {
            musicManager = new DJ(playerManager);
            musicManagers.put(guild.getLongID(), musicManager);
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

    public void join(final IVoiceChannel channel){
        DJ musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.joinChannel(channel);
    }

    public void queue(final IGuild guild, final String trackURL){
        DJ musicManager = getGuildAudioPlayer(guild);
        musicManager.queue();
    }




    public void loadAndPlay(final IChannel channel, final String trackUrl) {
        DJ musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                ChannelUtil.sendMessage(channel, "Adding to queue " + track.getInfo().title);

                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                ChannelUtil.sendMessage(channel, "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")");

                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                throw new IllegalArgumentException("Nothing found by " + trackUrl);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                throw new IllegalArgumentException("Could not play: " + exception.getMessage());
            }
        });
    }
}
