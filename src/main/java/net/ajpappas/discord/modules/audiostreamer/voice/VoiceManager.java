package net.ajpappas.discord.modules.audiostreamer.voice;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.ajpappas.discord.api.commands.exceptions.*;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.modules.audiostreamer.playlists.Playlist;
import net.ajpappas.discord.utils.ChannelUtil;
import net.ajpappas.discord.utils.ExceptionMessage;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
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

    public void joinChannel(IGuild guild, IUser requester, IVoiceChannel channel) throws CommandStateException, CommandBotPermissionException {
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
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

    public void leaveChannel(IGuild guild, IUser requester) throws CommandPermissionException, CommandStateException {
        if (!AudioStreamer.hasDJPermissions(requester, guild))
            throw new CommandPermissionException("You must be a \"" + AudioStreamer.ADMIN_ROLE + "\" to execute this command!");
        if (AudioStreamer.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("I am not connected to a voice channel!");
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
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

    public void queue(IGuild guild, IUser requester, String songID) throws CommandPermissionException, CommandRuntimeException, CommandStateException {
        if (songID.toLowerCase().contains("twitch.tv") && !AudioStreamer.hasAdminPermissions(requester, guild))
            throw new CommandPermissionException("You must be a \"" + AudioStreamer.ADMIN_ROLE + "\" to add Twitch.tv live streams!");
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && !requester.getVoiceStateForGuild(guild).getChannel().equals(getDJ(guild).getVoiceChannel()))
            throw new CommandStateException("You must be in my voice channel to control the music!");
        DJ dj = getDJ(guild);
        if (( dj.getPlaying() != null && songID.contains(dj.getPlaying().getIdentifier())) || dj.getQueue().stream().anyMatch(t -> songID.contains(t.getIdentifier()))) {
            throw new CommandStateException("That song is already in the queue!");
        }
        playerManager.loadItemOrdered(dj, songID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                dj.queue(requester, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    dj.queue(requester, track);
                }
            }

            @Override
            public void noMatches() {
                throw new CommandRuntimeException("Sorry, I was unable to find the song you specified.");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (exception.severity == FriendlyException.Severity.COMMON)
                    throw new CommandRuntimeException(exception.getMessage());
                throw new CommandRuntimeException("Sorry, an error occurred while loading your song. Please try again later.");
            }
        });
    }

    public void queue(IGuild guild, IUser requester, Playlist playlist) throws CommandStateException, CommandPermissionException {
        if (playlist == null)
            throw new CommandStateException("You do not have a selected playlist!");
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
        for (String songURL : playlist.getSongIDs()) {
            queue(guild, requester, songURL);
        }
    }

    public void dequeue(IGuild guild, IUser requester, String songID) throws CommandPermissionException, CommandStateException {
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
        DJ dj = getDJ(guild);
        if (!(AudioStreamer.hasDJPermissions(requester, guild) || dj.getTrackRequester(songID).equals(requester)))
            throw new CommandPermissionException("You do not have permission to remove this song!");
        dj.dequeue(songID);
    }

    public void dequeue(IGuild guild, IUser requester, Playlist playlist) throws CommandPermissionException, CommandStateException {
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
        DJ dj = getDJ(guild);
        if (AudioStreamer.hasAdminPermissions(requester, guild)) {
            playlist.getSongIDs().forEach(s -> dj.dequeue(s));
            return;
        }
        for (String s : playlist.getSongIDs())
            dequeue(guild, requester, s);
    }

    public boolean skip(IGuild guild, IUser requester, boolean force, int count) throws CommandPermissionException, CommandStateException {
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
        if (force) {
            if (!AudioStreamer.hasDJPermissions(requester, guild))
                throw new CommandPermissionException("You do not have permission to force skip songs!");
            getDJ(guild).skipCurrentTrack(count);
            return true;
        }
        return getDJ(guild).skip(requester);
    }

    public void pause(IGuild guild, IUser requester) throws CommandPermissionException, CommandStateException {
        if (!AudioStreamer.hasDJPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
        getDJ(guild).pause();
    }

    public void unpause(IGuild guild, IUser requester) throws CommandPermissionException, CommandStateException {
        if (!AudioStreamer.hasDJPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
        getDJ(guild).unpause();
    }

    public boolean isPaused(IGuild guild) {
        return getDJ(guild).isPaused();
    }

    public boolean isPlayingRandom(IGuild guild) {
        return getDJ(guild).isPlayingRandom();
    }

    public List<AudioTrack> getQueue(IGuild guild) {
        return getDJ(guild).getQueue();
    }

    public void shuffle(IGuild guild, IUser requester) throws CommandPermissionException {
        if (!AudioStreamer.hasDJPermissions(requester, guild))
            throw new CommandPermissionException(ExceptionMessage.PERMISSION_DENIED);
        getDJ(guild).shuffle();
    }

    public void playRandom(IGuild guild, IUser requester) {
        if (getDJ(guild).isPlayingRandom())
            getDJ(guild).setPlayingRandom(false, null);
        else
            getDJ(guild).setPlayingRandom(true, requester);
    }

    public AudioTrack getNowPlaying(IGuild guild) {
        return getDJ(guild).getPlaying();
    }

    public void putNowPlayingMessage(IMessage message, AudioTrack track) {
        getDJ(message.getGuild()).addNewTrackMessage(message, track);
    }

    /**
     * @param guild
     * @param requester
     * @param volume    int from 0 - 150
     */
    public void setVolume(IGuild guild, IUser requester, int volume) throws CommandPermissionException, CommandStateException {
        if (!AudioStreamer.hasAdminPermissions(requester, guild))
            throw new CommandPermissionException("You do not have permission to change the volume!");
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");
        getDJ(guild).setVolume(volume);
    }

    public IUser getTrackRequester(IGuild guild, AudioTrack track) {
        return getDJ(guild).getTrackRequester(track);
    }

    public void clearQueue(IGuild guild, IUser requester) throws CommandPermissionException, CommandStateException {
        if (!AudioStreamer.hasAdminPermissions(requester, guild))
            throw new CommandPermissionException("You do not have permission to clear the queue!");
        if (requester.getVoiceStateForGuild(guild).getChannel() == null)
            throw new CommandStateException("You must be connected to a voice channel to execute this command!");
        if (getDJ(guild).getVoiceChannel() != null && requester.getVoiceStateForGuild(guild).getChannel() != getDJ(guild).getVoiceChannel())
            throw new CommandStateException("You must be in my voice channel to control the music!");

        getDJ(guild).clearQueue();
    }

    public String getSongTitle(String songURL) {
        Semaphore s = new Semaphore(0);

        Info title = new Info();

        playerManager.loadItem(songURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                title.title = track.getInfo().title;
                s.release();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                s.release();
            }

            @Override
            public void noMatches() {
                s.release();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                s.release();
            }
        });
        s.acquireUninterruptibly();
        return title.title;
    }

    private class Info {
        public String title;
    }

    /* Listeners */
    @EventSubscriber
    public void reactionEvent(ReactionAddEvent e) {
        if (e.getUser().equals(AudioStreamer.getClient().getOurUser()))
            return;

        DJ dj = getDJ(e.getGuild());
        IMessage message = e.getMessage();
        AudioTrack track = null;

        if (message.equals(dj.getNowPlayingMessage()))
            track = dj.getPlaying();
        else if (dj.getTrackMessages().containsKey(message))
            track = dj.getTrackMessages().get(message);

        if (track == null)
            return;

        switch (e.getReaction().getUnicodeEmoji().getAliases().get(0)) {
            case "black_right_pointing_double_triangle_with_vertical_bar":
                if (track.getIdentifier().equals(dj.getPlaying().getIdentifier())) {
                    try {
                        skip(e.getGuild(), e.getUser(), false, 1);
                    } catch (CommandException ex) {
                        ChannelUtil.sendMessage(e.getChannel(), ex.getMessage());
                    }
                } else {
                    ChannelUtil.sendMessage(e.getChannel(), e.getUser().getName() + ", You can only skip currently playing songs");
                    return;
                }
                break;
            case "star":
                try {
                    getDJ(e.getGuild()).starSong(track, e.getUser());
                } catch (CommandException ex) {
                    ChannelUtil.sendMessage(e.getChannel(), ex.getMessage());
                }
                break;
        }
    }

    @EventSubscriber
    public void removeReactionEvent(ReactionRemoveEvent e) {
        if (e.getUser().equals(AudioStreamer.getClient().getOurUser()))
            return;

        switch (e.getReaction().getUnicodeEmoji().getAliases().get(0)) {
            case "black_right_pointing_double_triangle_with_vertical_bar":
                getDJ(e.getGuild()).unskip(e.getUser());
                break;
            case "star":
                try {
                    getDJ(e.getGuild()).removeStar(e.getMessage(), e.getUser());
                } catch (CommandException ex) {
                    ChannelUtil.sendMessage(e.getChannel(), ex.getMessage());
                }
                break;
        }
    }
}
