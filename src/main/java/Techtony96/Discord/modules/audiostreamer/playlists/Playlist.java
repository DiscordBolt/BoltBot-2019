package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.api.commands.exceptions.CommandStateException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/15/2017.
 */
public class Playlist {

    private String title, ownerID, guildID;
    private List<String> contributors = new ArrayList<>();
    //private List<String> songIDs = new ArrayList<>();
    private HashMap<String, String> songs = new HashMap<>();

    Playlist(String title, IUser owner, IGuild guild) {
        this.title = title;
        this.ownerID = owner.getStringID();
        this.guildID = guild.getStringID();
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

    public List<String> getSongIDs() {
        return new ArrayList<>(songs.keySet());
    }

    public String getSongTitle(String songID) {
        return songs.get(songID);
    }

    public void setSongTitle(String sondID, String songTitle) {
        songs.put(sondID, songTitle);
        PlaylistManager.writePlaylistFile(this);
    }

    public void addContributor(IUser user) throws CommandStateException {
        if (contributors.contains(user))
            throw new CommandStateException(user.getName() + " is already a contributor to " + this.getTitle() + ".");
        contributors.add(user.getID());
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeContributor(IUser user) throws CommandStateException {
        if (contributors.remove(user) == false)
            throw new CommandStateException(user.getName() + " is not a contributor to " + this.getTitle() + ".");
        PlaylistManager.writePlaylistFile(this);
    }

    public void addSong(String songID) throws CommandStateException {
        if (songs.containsKey(songID))
            throw new CommandStateException("That song is already in this playlist!");
        songs.put(songID, songID);
        AudioStreamer.getVoiceManager().loadSong(this, songID);
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(String songID) throws CommandStateException {
        if (!songs.containsKey(songID))
            throw new CommandStateException("That song is not in this playlist!");
        songs.remove(songID);
        PlaylistManager.writePlaylistFile(this);
    }
}
