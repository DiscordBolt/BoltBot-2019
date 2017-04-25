package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.api.commands.exceptions.CommandBotPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandRuntimeException;
import Techtony96.Discord.api.commands.exceptions.CommandStateException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.playlists.Playlist;
import Techtony96.Discord.utils.ExceptionMessage;
import Techtony96.Discord.utils.Logger;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

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
            getDJ(guild).setVoiceChannel(channel);
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
        dj.getVoiceChannel().leave();
        dj.setVoiceChannel(null);
        dj.clearQueue();
    }

    public void forceLeaveChannel(IGuild guild) {
        DJ dj = getDJ(guild);
        dj.getVoiceChannel().leave();
        dj.setVoiceChannel(null);
        dj.clearQueue();
    }

    public String queue(IGuild guild, IUser requestor, String songID) throws CommandPermissionException {
        if (songID.toLowerCase().contains("twitch.tv") && !AudioStreamer.hasAdminPermissions(requestor, guild))
            throw new CommandPermissionException("You must be a \"" + AudioStreamer.ADMIN_ROLE + "\" to add Twitch.tv live streams!");

        DJ dj = getDJ(guild);
        Semaphore wait = new Semaphore(0);
        final String[] songTitle = {"Unable to get song title"};
        playerManager.loadItemOrdered(dj, songID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                dj.queue(requestor, track);
                songTitle[0] = track.getInfo().title;
                wait.release();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    dj.queue(requestor, track);
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
        try {
            wait.acquire();
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
        }
        return songTitle[0];
    }

    public void queue(IGuild guild, IUser requestor, Playlist playlist) throws CommandStateException, CommandPermissionException {
        if (playlist == null)
            throw new CommandStateException("You do not have a selected playlist!");
        for (String s : playlist.getSongIDs())
            queue(guild, requestor, s);
    }

    public void dequeue(IGuild guild, IUser requestor, String songID) throws CommandPermissionException {
        DJ dj = getDJ(guild);
        if (!AudioStreamer.hasAdminPermissions(requestor, guild) || !dj.getTrackRequester(songID).equals(requestor))
            throw new CommandPermissionException("You do not have permission to remove this song!");
        dj.dequeue(songID);
    }

    public void dequeue(IGuild guild, IUser requestor, Playlist playlist) throws CommandPermissionException {
        DJ dj = getDJ(guild);
        if (AudioStreamer.hasAdminPermissions(requestor, guild)) {
            playlist.getSongIDs().forEach(s -> dj.dequeue(s));
            return;
        }
        for (String s : playlist.getSongIDs())
            dequeue(guild, requestor, s);
    }

    private Set<IUser> votesToSkip = new HashSet<>();

    public boolean skip(IGuild guild, IUser requestor, boolean force) throws CommandPermissionException, CommandStateException {
        if (force) {
            if (!AudioStreamer.hasAdminPermissions(requestor, guild))
                throw new CommandPermissionException("You do not have permission to force skip songs!");
            getDJ(guild).skipCurrentTrack();
            return true;
        }
        if (!votesToSkip.add(requestor))
            throw new CommandStateException("You have already voted to skip the current song!");
        // Filter out users who have voted but are no longer connected to the voice channel
        votesToSkip.removeIf(u -> u.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel());
        if ((double) votesToSkip.size() / (double) (getDJ(guild).getVoiceChannel().getConnectedUsers().size() - 1) >= AudioStreamer.VOTE_SKIP_PERCENT) {
            getDJ(guild).skipCurrentTrack();
            votesToSkip.clear();
            return true;
        }
        return false;
    }

    public void pause(IGuild guild, IUser requester) throws CommandPermissionException {
        if (!AudioStreamer.hasAdminPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        getDJ(guild).pause();
    }

    public void unpause(IGuild guild, IUser requester) throws CommandPermissionException {
        if (!AudioStreamer.hasAdminPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        getDJ(guild).unpause();
    }

    public List<AudioTrack> getQueue(IGuild guild) {
        return getDJ(guild).getQueue();
    }

    public void loadSong(Playlist playlist, String songID) {
        playerManager.loadItem(songID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                playlist.addSong(track);
                throw new CommandRuntimeException("Successfully added " + track.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                audioPlaylist.getTracks().forEach(t -> playlist.addSong(t));
                throw new CommandRuntimeException("Successfully added songs from " + audioPlaylist.getName());
            }

            @Override
            public void noMatches() {
                playlist.forceRemoveSong(songID);
                throw new CommandRuntimeException("Could not find any media for " + songID);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                playlist.forceRemoveSong(songID);
                if (exception.severity == FriendlyException.Severity.COMMON)
                    throw new CommandRuntimeException(exception.getMessage());
                throw new CommandRuntimeException("An error occured while loading your song.");
            }
        });
    }

    public AudioTrack getNowPlaying(IGuild guild) {
        return getDJ(guild).getPlaying();
    }

    /**
     * @param guild
     * @param requestor
     * @param volume    int from 0 - 150
     */
    public void setVolume(IGuild guild, IUser requestor, int volume) throws CommandPermissionException {
        if (!AudioStreamer.hasAdminPermissions(requestor, guild))
            throw new CommandPermissionException("You do not have permission to change the volume!");
        getDJ(guild).setVolume(volume);
    }
}
