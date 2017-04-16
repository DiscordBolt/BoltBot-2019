package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.songs.Song;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

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
    private Set<Song> songs = new HashSet<>();

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

    public boolean addContributor(IUser user) {
        // TODO update pl file
        return contributors.add(user.getID());
    }

    public boolean removeContributor(IUser user) {
        // TODO update pl file
        return contributors.remove(user);
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public boolean addSong(Song song) {
        // TODO update pl file
        return songs.add(song);
    }

    public boolean removeSong(Song song) {
        // TODO update pl file
        return songs.remove(song);
    }
}
