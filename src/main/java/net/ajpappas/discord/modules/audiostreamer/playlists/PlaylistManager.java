package net.ajpappas.discord.modules.audiostreamer.playlists;

import com.google.gson.Gson;
import net.ajpappas.discord.api.commands.exceptions.CommandArgumentException;
import net.ajpappas.discord.api.commands.exceptions.CommandPermissionException;
import net.ajpappas.discord.api.commands.exceptions.CommandStateException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.utils.Logger;
import net.ajpappas.discord.utils.UserUtil;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/15/2017.
 */
public class PlaylistManager {

    private static final Path PLAYLIST_DIRECTORY = Paths.get(System.getProperty("user.dir"), "playlists");
    private static final Gson g = new Gson();
    private static HashMap<Long, Playlist> selectedPlaylist = new HashMap<>();

    private List<Playlist> playlists = new ArrayList<>();

    public PlaylistManager() {
        loadPlaylists();
    }

    private void loadPlaylists() {
        try {
            if (!PLAYLIST_DIRECTORY.toFile().exists())
                Files.createDirectories(PLAYLIST_DIRECTORY);
            Files.walk(PLAYLIST_DIRECTORY).forEach(p -> {
                try {
                    if (!Files.isDirectory(p))
                        playlists.add(g.fromJson(new FileReader(p.toFile()), Playlist.class));
                } catch (FileNotFoundException e) {
                    Logger.error("Unable to load playlist \"" + p.getFileName().toString() + "\"");
                    Logger.debug(e);
                }
            });
            Collections.sort(playlists);
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
        Collections.sort(playlists);
        return pl;
    }

    public boolean deletePlaylist(String title, IUser requester) throws CommandArgumentException, CommandPermissionException, CommandStateException {
        Optional<Playlist> playlist = getPlaylist(title);
        if (!playlist.isPresent())
            throw new CommandArgumentException("Playlist \"" + title + "\" does not exist!");

        if (!(UserUtil.hasRole(requester, playlist.get().getGuild(), AudioStreamer.ADMIN_ROLE) || playlist.get().getOwnerID().equals(requester.getStringID())))
            throw new CommandPermissionException(requester.getName() + " is not the owner of this playlist!");

        if (!getPlaylistPath(title).toFile().delete())
            throw new CommandStateException("Unable to delete playlist file.");
        return playlists.removeIf(p -> p.getTitle().equalsIgnoreCase(title));
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public Playlist getPlaylist(int index) throws CommandArgumentException {
        if (index < 0 || index > playlists.size() - 1)
            throw new CommandArgumentException((index + 1) + " is not a valid index!");
        return playlists.get(index);
    }

    public Optional<Playlist> getPlaylist(String title) {
        return playlists.stream().filter(p -> p.getTitle().equalsIgnoreCase(title)).findAny();
    }

    public List<Playlist> getPlaylists(IUser owner) {
        return playlists.stream().filter(p -> p.getOwner().equals(owner)).collect(Collectors.toList());
    }

    public Playlist getSelectedPlaylist(Long userID) {
        return selectedPlaylist.get(userID);
    }

    public void setSelectedPlaylist(Long userID, Playlist playlist) {
        selectedPlaylist.put(userID, playlist);
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
