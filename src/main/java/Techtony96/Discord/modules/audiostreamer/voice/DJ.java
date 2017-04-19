package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.voice.internal.AudioProvider;
import Techtony96.Discord.modules.audiostreamer.voice.internal.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;

/**
 * Created by Tony on 4/16/2017.
 */
public class DJ {

    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;

    /**
     * Creates a player and a track scheduler.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    public DJ(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioProvider getAudioProvider() {
        return new AudioProvider(player);
    }

    public void queue(AudioTrack track){
        scheduler.queue(track);
    }

    public void joinChannel(IVoiceChannel channel){
        try {
            channel.join();
        } catch (MissingPermissionsException e){
            throw new IllegalArgumentException("I do not have permissions to join your voice channel!");
        }

    }

    public void startPlaying(){
        if (!scheduler.hasTrack())
            throw new IllegalStateException("There are no songs in the queue!");

        scheduler.
    }

    public void skipTrack(){
        scheduler.nextTrack();
    }
}
