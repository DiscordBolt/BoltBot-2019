package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.modules.audiostreamer.songs.Song;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tony on 4/15/2017.
 */
public class PlayList {

    private String title;
    private IUser owner;
    private Set<IUser> contributors = new HashSet<>();
    private Set<Song> songs = new HashSet<>();

    PlayList(String title, IUser owner) {
        this.title = title;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public IUser getOwner() {
        return owner;
    }

    public Set<IUser> getContributors() {
        return contributors;
    }

    public boolean addContributor(IUser user) {
        // TODO update pl file
        return contributors.add(user);
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
