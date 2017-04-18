package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/15/2017.
 */
public class Playlist {

    private String title;
    private String ownerID;
    private String guildID;
    private List<String> contributors = new ArrayList<>();
    private List<Song> songs = new ArrayList<>();

    Playlist(String title, IUser owner, IGuild guild) {
        this.title = title;
        this.ownerID = owner.getID();
        this.guildID = guild.getID();
    }

    public String getTitle() {
        return title;
    }

    public IUser getOwner() {
        return AudioStreamer.getClient().getUserByID(ownerID);
    }

    public IGuild getGuild() {
        return AudioStreamer.getClient().getGuildByID(guildID);
    }

    public ArrayList<IUser> getContributors() {
        return contributors.stream().map(u -> AudioStreamer.getClient().getUserByID(u)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void addContributor(IUser user) throws IllegalArgumentException {
        if (contributors.contains(user))
            throw new IllegalArgumentException(user.getName() + " is already a contributor to " + getTitle() + ".");
        contributors.add(user.getID());
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeContributor(IUser user) throws IllegalArgumentException {
        if (contributors.remove(user) == false)
            throw new IllegalArgumentException(user.getName() + " is not a contributor to " + getTitle() + ".");
        PlaylistManager.writePlaylistFile(this);
    }

    public Song addSong(Song song) {
        if (songs.contains(song))
            throw new IllegalArgumentException("\"" + song.getTitle() + "\" is already in this playlist!");
        songs.add(song);
        PlaylistManager.writePlaylistFile(this);
        return song;
    }

    public Song removeSong(Song song) {
        if (!songs.contains(song))
            throw new IllegalArgumentException("\"" + song.getTitle() + "\" is not in this playlist!");
        songs.remove(song);
        PlaylistManager.writePlaylistFile(this);
        return song;
    }

    public void removeSong(int index) {
        if (index < 0 || index >= songs.size())
            throw new IllegalArgumentException(index + 1 + " is not a valid index for this playlist!");
        songs.remove(index);
        PlaylistManager.writePlaylistFile(this);
    }
}
