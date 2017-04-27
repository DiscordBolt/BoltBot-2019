package Techtony96.Discord.modules.audiostreamer.voice;

import Techtony96.Discord.api.commands.exceptions.*;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.playlists.Playlist;
import Techtony96.Discord.utils.ChannelUtil;
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
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.HashMap;
import java.util.List;
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
        if (!AudioStreamer.hasDJPermissions(requestor, guild))
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



    public boolean skip(IGuild guild, IUser requester, boolean force) throws CommandPermissionException, CommandStateException {
        if (force) {
            if (!AudioStreamer.hasDJPermissions(requester, guild))
                throw new CommandPermissionException("You do not have permission to force skip songs!");
            getDJ(guild).skipCurrentTrack();
            return true;
        }
        return getDJ(guild).skip(requester);
    }

    public void pause(IGuild guild, IUser requester) throws CommandPermissionException {
        if (!AudioStreamer.hasDJPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        getDJ(guild).pause();
    }

    public void unpause(IGuild guild, IUser requester) throws CommandPermissionException {
        if (!AudioStreamer.hasDJPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        getDJ(guild).unpause();
    }

    public List<AudioTrack> getQueue(IGuild guild) {
        return getDJ(guild).getQueue();
    }

    public void shuffle(IGuild guild, IUser requester) throws CommandPermissionException {
        if (!AudioStreamer.hasAdminPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        getDJ(guild).shuffle();
    }

    public void loadSong(Playlist playlist, String songID) {
        playerManager.loadItem(songID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                playlist.forceAddSong(track);
                throw new CommandRuntimeException("Successfully added " + track.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                audioPlaylist.getTracks().forEach(t -> playlist.forceAddSong(t));
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


    /* Listeners */
    @EventSubscriber
    public void reactionEvent(ReactionAddEvent e){
        if (e.getUser().equals(AudioStreamer.getClient().getOurUser()))
            return;
        if (!e.getMessage().equals(getDJ(e.getGuild()).getNowPlayingMessage()))
            return;

        switch (e.getReaction().getUnicodeEmoji().getAliases().get(0)){
            case "black_right_pointing_double_triangle_with_vertical_bar":
                try {
                    skip(e.getGuild(), e.getUser(), false);
                } catch (CommandException ex) {
                    ChannelUtil.sendMessage(e.getChannel(), ex.getMessage());
                }
                break;
            case "star":
                try {
                    getDJ(e.getGuild()).starSong(e.getMessage(), e.getUser());
                } catch (CommandException ex) {
                    ChannelUtil.sendMessage(e.getChannel(), ex.getMessage());
                }
        }

    }

    @EventSubscriber
    public void removeReactionEvent(ReactionRemoveEvent e){
        if (e.getUser().equals(AudioStreamer.getClient().getOurUser()))
            return;
        if (!e.getMessage().equals(getDJ(e.getGuild()).getNowPlayingMessage()))
            return;

        switch (e.getReaction().getUnicodeEmoji().getAliases().get(0)){
            case "black_right_pointing_double_triangle_with_vertical_bar":
                getDJ(e.getGuild()).unskip(e.getUser());
                break;
            case "star":
                try {
                    getDJ(e.getGuild()).removeStar(e.getMessage(), e.getUser());
                } catch (CommandException ex) {
                    ChannelUtil.sendMessage(e.getChannel(), ex.getMessage());
                }
        }
    }
}
