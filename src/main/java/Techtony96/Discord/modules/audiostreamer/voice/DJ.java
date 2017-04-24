package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.voice.internal.AudioProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Tony on 4/16/2017.
 */
public class DJ extends AudioEventAdapter {

    private AudioPlayer player;
    private IGuild guild;
    private final BlockingQueue<AudioTrack> queue;
    private HashMap<String, IUser> trackOwners;
    private IVoiceChannel connectedChannel;

    public DJ(IGuild guild, AudioPlayer player) {
        this.guild = guild;
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.trackOwners = new HashMap<>();
        player.addListener(this);
        AudioStreamer.getClient().getDispatcher().registerListener(this);
        guild.getAudioManager().setAudioProvider(new AudioProvider(player));
        setVolume(10);
    }

    public void queue(IUser requestor, AudioTrack track) {
        if (getConnectedVoiceChannel() == null) {
            setConnectVoiceChannel(requestor.getVoiceStateForGuild(guild).getChannel());
            if (getConnectedVoiceChannel() != null)
                getConnectedVoiceChannel().join();
        }
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        trackOwners.put(track.getIdentifier(), requestor);
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    public void clearQueue() {
        queue.clear();
        player.stopTrack();
    }

    public void removeQueue(String songID){
        queue.removeIf(a -> a.getIdentifier().equalsIgnoreCase(songID));
    }

    public boolean hasTrack() {
        return !queue.isEmpty();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Remove the track after it is over
        trackOwners.remove(track);
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
        trackOwners.remove(track);
        nextTrack();
    }

    public void setConnectVoiceChannel(IVoiceChannel channel) {
        this.connectedChannel = channel;
    }

    public IVoiceChannel getConnectedVoiceChannel() {
        return connectedChannel;
    }

    public IUser getTrackOwner(String songID){
        return trackOwners.get(songID);
    }

    public List<AudioTrack> getQueue(){
        return new ArrayList<>(queue);
    }

    public AudioTrack getPlaying() {
        return player.getPlayingTrack();
    }

    /**
     *
     * @param volume 0 - 150
     */
    public void setVolume(int volume){
        player.setVolume(volume);
    }

    private void checkChannel() {
        if (getConnectedVoiceChannel().getConnectedUsers().size() <= 1) {
            AudioStreamer.getVoiceManager().forceLeaveChannel(guild);
        }
    }

    @EventSubscriber
    public void watchChannel(GuildLeaveEvent e) {
        checkChannel();
    }

    @EventSubscriber
    public void watchChannel(UserVoiceChannelLeaveEvent e) {
        if (e.getVoiceChannel().equals(getConnectedVoiceChannel())) {
            checkChannel();
        }
    }

    @EventSubscriber
    public void watchChannel(UserVoiceChannelMoveEvent e) {
        if (e.getOldChannel().equals(getConnectedVoiceChannel())) {
            checkChannel();
        }
    }
}
