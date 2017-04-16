package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.utils.UserUtil;
import com.google.gson.Gson;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/15/2017.
 */
public class PlaylistManager {

    private static final Path PLAYLIST_DIRECTORY = Paths.get(System.getProperty("user.home"), "discord", "playlists");
    private static final Gson g = new Gson();

    private HashSet<Playlist> playlists = new HashSet<>();

    public PlaylistManager() {

        try {
            Files.walk(PLAYLIST_DIRECTORY).forEach(p -> {
                try {
                    playlists.add(g.fromJson(new FileReader(p.toFile()), Playlist.class));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Playlist createPlaylist(String title, IUser owner, IGuild guild) {
        if (playlists.stream().filter(p -> p.getTitle().equalsIgnoreCase(title)).findAny().isPresent())
            throw new IllegalArgumentException("There already exists a playlist with that title!");

        Playlist pl = new Playlist(title, owner, guild);

        //TODO save playlist to file
        File file = getPlaylistPath(title).toFile();
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(g.toJson(pl));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playlists.add(pl);
        return pl;
    }

    public boolean deletePlaylist(String title, IUser requestor) {
        Optional<Playlist> playlist = getPlaylist(title);
        if (!playlist.isPresent())
            throw new IllegalArgumentException("Playlist \"" + title + "\" does not exist!");

        if (!UserUtil.hasRole(requestor, playlist.get().getGuild(), AudioStreamer.ADMIN_ROLE) || !playlist.get().getOwner().getID().equals(requestor.getID()))
            throw new IllegalArgumentException(requestor.getName() + " is not the owner of this playlist!");

        // TODO delete playlist file
        return playlists.removeIf(p -> p.getTitle().equalsIgnoreCase(title));
    }

    public Set<Playlist> getPlaylists() {
        return playlists;
    }

    public Optional<Playlist> getPlaylist(String title) {
        return playlists.stream().filter(p -> p.getTitle().equalsIgnoreCase(title)).findAny();
    }

    public Set<Playlist> getPlaylists(IUser owner) {
        return playlists.stream().filter(p -> p.getOwner().getID().equals(owner.getID())).collect(Collectors.toSet());
    }


    /* FILE IO */

    private static Path getPlaylistPath(String title) {
        return Paths.get(PLAYLIST_DIRECTORY.toString(), title + ".json");
    }
}
