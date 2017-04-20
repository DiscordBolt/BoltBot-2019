package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.api.commands.exceptions.CommandBotPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandRuntimeException;
import Techtony96.Discord.api.commands.exceptions.CommandStateException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.playlists.Playlist;
import Techtony96.Discord.modules.audiostreamer.playlists.Song;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Tony on 4/16/2017.
 */
public class VoiceManager {

    private HashMap<Long, DJ> djMap = new HashMap<>();
    private AudioPlayerManager playerManager;

    public VoiceManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        playerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        playerManager.getConfiguration().setOutputFormat(StandardAudioDataFormats.DISCORD_OPUS);
    }

    private synchronized DJ getDJ(IGuild guild) {
        return djMap.computeIfAbsent(guild.getLongID(), k -> new DJ(guild, playerManager.createPlayer()));
    }

    public void joinChannel(IGuild guild, IUser requestor, IVoiceChannel channel) throws CommandStateException, CommandBotPermissionException {
        if (requestor.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be in a voice channel for me to join!");
        if (AudioStreamer.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel() != null)
            throw new CommandStateException("I am already connect to a voice channel!");

        try {
            channel.join();
            getDJ(guild).setConnectVoiceChannel(channel);
        } catch (MissingPermissionsException e) {
            throw new CommandBotPermissionException();
        }
    }

    public void leaveChannel(IGuild guild, IUser requestor) throws CommandPermissionException, CommandStateException {
        if (!AudioStreamer.hasAdminPermissions(requestor, guild))
            throw new CommandPermissionException("You must be a \"" + AudioStreamer.ADMIN_ROLE + "\" to execute this command!");
        if (AudioStreamer.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("I am not connected to a voice channel!");

        DJ dj = getDJ(guild);
        dj.getConnectedVoiceChannel().leave();
        dj.setConnectVoiceChannel(null);
        dj.clearQueue();
    }

    public void queue(IGuild guild, IUser requestor, Song song) {
        DJ dj = getDJ(guild);
        playerManager.loadItemOrdered(dj, song.getId(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                song.setTitle(track.getInfo().title);
                dj.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    dj.queue(track);
                }
            }

            @Override
            public void noMatches() {
                throw new CommandRuntimeException("Sorry, I was unable to play the song you specified.");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (exception.severity == FriendlyException.Severity.COMMON)
                    throw new CommandRuntimeException(exception.getMessage());
                throw new CommandRuntimeException("Sorry, an error occurred while loading your song. Please try again later.");
            }
        });
    }

    public void queue(IGuild guild, IUser requestor, Playlist playlist) {
        playlist.getSongs().stream().forEach(s -> queue(guild, requestor, s));
    }

    public void dequeue(IGuild guild, IUser requestor, Song song) {
        //remove the requested song from queue, if admin or put into queue
    }

    public void dequeue(IGuild guild, IUser requestor, Playlist playlist) {
        //remove the requested pl from queue, if admin or put into queue
    }

    public void skip(IGuild guild, IUser requestor, boolean force) {
        //Start vote to skip, check perms if force is true
    }

    public void pause(IGuild guild, IUser requestor) {
        //TODO
    }

    public void resume(IGuild guild, IUser requestor) {
        //TODO
    }

    public List<Song> getQueue(IGuild guild) {
        return null;
    }

    public void setVolume(IGuild guild, IUser requestor) {
        //check admin
    }


}
