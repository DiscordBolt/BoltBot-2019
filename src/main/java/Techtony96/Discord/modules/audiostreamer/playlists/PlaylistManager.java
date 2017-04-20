package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.api.commands.exceptions.CommandArgumentException;
import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandStateException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.utils.Logger;
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

    private static final Path PLAYLIST_DIRECTORY = Paths.get(System.getProperty("user.dir"), "playlists");
    private static final Gson g = new Gson();

    private HashSet<Playlist> playlists = new HashSet<>();

    public PlaylistManager() {
        try {
            Files.walk(PLAYLIST_DIRECTORY).forEach(p -> {
                try {
                    if (!Files.isDirectory(p))
                        playlists.add(g.fromJson(new FileReader(p.toFile()), Playlist.class));
                } catch (FileNotFoundException e) {
                    Logger.error("Unable to load playlist \"" + p.getFileName().toString() + "\"");
                    Logger.debug(e);
                }
            });
        } catch (IOException e) {
            Logger.error("Unable to walk playlist directory.");
            Logger.debug(e);
        }
    }

    public Playlist createPlaylist(String title, IUser owner, IGuild guild) throws CommandStateException {
        if (playlists.stream().filter(p -> p.getTitle().equalsIgnoreCase(title)).findAny().isPresent())
            throw new CommandStateException("There already exists a playlist with that title!");

        Playlist pl = new Playlist(title, owner, guild);

        // Saves the playlist to disk
        writePlaylistFile(pl);

        playlists.add(pl);
        return pl;
    }

    public boolean deletePlaylist(String title, IUser requestor) throws CommandArgumentException, CommandPermissionException {
        Optional<Playlist> playlist = getPlaylist(title);
        if (!playlist.isPresent())
            throw new CommandArgumentException("Playlist \"" + title + "\" does not exist!");

        if (!UserUtil.hasRole(requestor, playlist.get().getGuild(), AudioStreamer.ADMIN_ROLE) || !playlist.get().getOwner().getID().equals(requestor.getID()))
            throw new CommandPermissionException(requestor.getName() + " is not the owner of this playlist!");

        getPlaylistPath(title).toFile().delete();
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

    protected static void writePlaylistFile(Playlist playlist) {
        File file = getPlaylistPath(playlist.getTitle()).toFile();
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(g.toJson(playlist));
            fw.close();
        } catch (IOException e) {
            Logger.error("Unable to write playlist \"" + playlist.getTitle() + "\" to file.");
            Logger.debug(e);
        }
    }
}
