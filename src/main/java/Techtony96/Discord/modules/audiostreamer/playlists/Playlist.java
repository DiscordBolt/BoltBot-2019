package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.songs.Song;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/15/2017.
 */
public class Playlist {

    private String title;
    private String ownerID;
    private String guildID;
    private Set<String> contributors = new HashSet<>();
    private ArrayList<Song> songs = new ArrayList<>();

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

    public Set<IUser> getContributors() {
        return contributors.stream().map(c -> AudioStreamer.getClient().getUserByID(c)).collect(Collectors.toSet());
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void addContributor(IUser user) {
        contributors.add(user.getID());
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeContributor(IUser user) {
        contributors.remove(user);
        PlaylistManager.writePlaylistFile(this);
    }

    public void addSong(Song song) {
        songs.add(song);
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(Song song) {
        songs.remove(song);
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(int index) {
        songs.remove(index);
        PlaylistManager.writePlaylistFile(this);
    }
}
