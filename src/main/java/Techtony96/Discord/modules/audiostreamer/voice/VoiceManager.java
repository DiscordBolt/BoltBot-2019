package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.api.commands.exceptions.CommandBotPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandRuntimeException;
import Techtony96.Discord.api.commands.exceptions.CommandStateException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.playlists.Playlist;
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

    public void queue(IGuild guild, IUser requestor, String songID) {
        DJ dj = getDJ(guild);
        playerManager.loadItemOrdered(dj, songID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                dj.queue(requestor, track);
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
    }

    public void queue(IGuild guild, IUser requestor, Playlist playlist) {
        playlist.getSongIDs().stream().forEach(s -> queue(guild, requestor, s));
    }

    public void dequeue(IGuild guild, IUser requestor, String songID) throws CommandPermissionException {
        DJ dj = getDJ(guild);
        if (!AudioStreamer.hasAdminPermissions(requestor, guild) || !dj.getTrackOwner(songID).equals(requestor))
            throw new CommandPermissionException("You do not have permission to remove this song!");
        dj.removeQueue(songID);
    }

    public void dequeue(IGuild guild, IUser requestor, Playlist playlist) throws CommandPermissionException {
        DJ dj = getDJ(guild);
        if (AudioStreamer.hasAdminPermissions(requestor, guild)) {
            playlist.getSongIDs().forEach(s -> dj.removeQueue(s));
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
            getDJ(guild).nextTrack();
            return true;
        }
        if (!votesToSkip.add(requestor))
            throw new CommandStateException("You have already voted to skip the current song!");
        // Filter out users who have voted but are no longer connected to the voice channel
        votesToSkip.removeIf(u -> u.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getConnectedVoiceChannel());
        if ((double) votesToSkip.size() / (double) (getDJ(guild).getConnectedVoiceChannel().getConnectedUsers().size() - 1) >= AudioStreamer.VOTE_SKIP_PERCENT) {
            getDJ(guild).nextTrack();
            votesToSkip.clear();
            return true;
        }
        return false;
    }

    public void pause(IGuild guild, IUser requestor) {
        //TODO
    }

    public void resume(IGuild guild, IUser requestor) {
        //TODO
    }

    public List<AudioTrack> getQueue(IGuild guild) {
        return getDJ(guild).getQueue();
    }

    public void loadSong(Playlist playlist, String songID) {
        playerManager.loadItem(songID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                playlist.setSongTitle(songID, track.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                audioPlaylist.getTracks().forEach(t -> playlist.setSongTitle(songID, t.getInfo().title));
            }

            @Override
            public void noMatches() {
                try {
                    playlist.removeSong(songID);
                } catch (CommandStateException e) {
                    // We didn't want the song in the playlist anyways
                }
                throw new CommandRuntimeException("Could not find any media for " + songID);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                try {
                    playlist.removeSong(songID);
                } catch (CommandStateException e) {
                    // We didn't want the song in the playlist anyways
                }
                if (exception.severity == FriendlyException.Severity.COMMON)
                    throw new CommandRuntimeException(exception.getMessage());
                throw new CommandRuntimeException("An error occured while loading your song.");
            }
        });
    }

    /**
     * @param guild
     * @param requestor
     * @param volume    int from 0 - 100
     */
    public void setVolume(IGuild guild, IUser requestor, int volume) throws CommandPermissionException {
        if (!AudioStreamer.hasAdminPermissions(requestor, guild))
            throw new CommandPermissionException("You do not have permission to change the volume!");
        getDJ(guild).setVolume(volume);
    }
}
