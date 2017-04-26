package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.voice.internal.AudioProvider;
import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.ExceptionMessage;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Tony on 4/16/2017.
 */
public class DJ extends AudioEventAdapter {

    private IGuild guild;
    private AudioPlayer player;
    private IVoiceChannel connectedChannel;
    private IChannel announceChannel;
    private IMessage nowPlayingMessage;

    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private HashMap<AudioTrack, IUser> trackOwners = new HashMap<>();

    public DJ(IGuild guild, AudioPlayer player) {
        this.guild = guild;
        this.player = player;
        player.addListener(this);
        AudioStreamer.getClient().getDispatcher().registerListener(this);
        guild.getAudioManager().setAudioProvider(new AudioProvider(player));
        setVolume(20);

        announceChannel = guild.getChannelsByName(AudioStreamer.TEXT_CHANNEL).stream().findAny().orElse(null);
    }


    public IGuild getGuild(){
        return guild;
    }

    public AudioPlayer getAudioPlayer(){
        return player;
    }

    public IMessage getNowPlayingMessage(){
        return nowPlayingMessage;
    }

    public IVoiceChannel getVoiceChannel(){
        return connectedChannel;
    }

    public void setVoiceChannel(IVoiceChannel channel){
        this.connectedChannel = channel;
    }

    public List<AudioTrack> getQueue(){
        return new ArrayList<>(queue);
    }

    public IUser getTrackRequester(AudioTrack track){
        return trackOwners.get(track);
    }

    public IUser getTrackRequester(String songID){
        return trackOwners.get(trackOwners.keySet().stream().filter(t -> t.getIdentifier().equalsIgnoreCase(songID)).findAny().orElse(null));
    }

    public void setVolume(int volume){
        player.setVolume(volume);
    }

    public void queue(IUser requester, AudioTrack track){
        // Connect to voice channel if not already
        if (getVoiceChannel() == null){
            setVoiceChannel(requester.getVoiceStateForGuild(getGuild()).getChannel());
            if (getVoiceChannel() != null)
                getVoiceChannel().join();
        }

        // Store who is requesting the AudioTrack
        trackOwners.put(track, requester);

        // Start playing the track, if a song is already playing, queue it up
        if (!player.startTrack(track, false))
            queue.offer(track);

    }

    public void skipCurrentTrack() {
        player.startTrack(queue.poll(), false);
    }

    public void clearQueue() {
        queue.clear();
        player.stopTrack();
    }

    public void dequeue(AudioTrack track){
        queue.remove(track);
    }

    public void dequeue(String songID){
        queue.removeIf(a -> a.getIdentifier().equalsIgnoreCase(songID));
    }

    public AudioTrack getPlaying() {
        return player.getPlayingTrack();
    }

    public void pause() {
        player.setPaused(true);
    }

    public void unpause() {
        player.setPaused(false);
    }

    /* Events */

    @Override
    public void onPlayerPause(AudioPlayer player) {
        //ChannelUtil.sendMessage(announceChannel, "Paused \"" + getPlaying().getInfo().title + "\".");
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        //ChannelUtil.sendMessage(announceChannel, "Resumed \"" + getPlaying().getInfo().title + "\".");
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(AudioStreamer.EMBED_COLOR);
        embed.withAuthorName(":musical_note: " + track.getInfo().title);
        embed.withAuthorUrl(track.getInfo().uri);
        embed.withDescription(trackOwners.get(track) != null ? "Requested by: " + trackOwners.get(track).getName() : "Unknown Requester");
        if (track.getInfo().uri.toLowerCase().contains("youtu")){
            embed.withThumbnail("https://img.youtube.com/vi/"+track.getIdentifier()+"/0.jpg");
        }


        nowPlayingMessage = ChannelUtil.sendMessageWait(announceChannel, embed.build());
        nowPlayingMessage.addReaction(":track_next:");



        ChannelUtil.sendMessage(announceChannel, "Started playing \"" + track.getInfo().title + "\".");
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Remove the track after it is over
        trackOwners.remove(track);
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            skipCurrentTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
        trackOwners.remove(track);
        skipCurrentTrack();
    }

    /* Channel Cleanup */

    private void checkChannel() {
        if (getVoiceChannel().getConnectedUsers().size() <= 1) {
            AudioStreamer.getVoiceManager().forceLeaveChannel(guild);
        }
    }

    @EventSubscriber
    public void watchChannel(GuildLeaveEvent e) {
        checkChannel();
    }

    @EventSubscriber
    public void watchChannel(UserVoiceChannelLeaveEvent e) {
        if (e.getVoiceChannel().equals(getVoiceChannel())) {
            checkChannel();
        }
    }

    @EventSubscriber
    public void watchChannel(UserVoiceChannelMoveEvent e) {
        if (e.getOldChannel().equals(getVoiceChannel())) {
            checkChannel();
        }
    }
}
