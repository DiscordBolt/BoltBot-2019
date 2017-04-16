package Techtony96.Discord.modules.audiostreamer.playlists;

import sx.blah.discord.handle.obj.IUser;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/15/2017.
 */
public class PlaylistManager {

    private HashSet<PlayList> playlists = new HashSet<>();

    public PlaylistManager() {
        // TODO load playlists from file
    }

    public PlayList createPlaylist(String title, IUser owner) {
        if (playlists.stream().filter(p -> p.getTitle().equalsIgnoreCase(title)).findAny().isPresent())
            throw new IllegalArgumentException("There already exists a playlist with that title!");

        PlayList pl = new PlayList(title, owner);
        //TODO save playlist to file
        playlists.add(pl);
        return pl;
    }

    public boolean deletePlaylist(String title) {
        // TODO delete playlist file
        return playlists.removeIf(p -> p.getTitle().equalsIgnoreCase(title));
    }

    public Set<PlayList> getPlaylists() {
        return playlists;
    }

    public Optional<PlayList> getPlaylist(String title) {
        return playlists.stream().filter(p -> p.getTitle().equalsIgnoreCase(title)).findAny();
    }

    public Set<PlayList> getPlaylists(IUser owner) {
        return playlists.stream().filter(p -> p.getOwner().getID().equals(owner.getID())).collect(Collectors.toSet());
    }
}
