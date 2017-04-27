package Techtony96.Discord.modules.audiostreamer.playlists;

import Techtony96.Discord.api.commands.exceptions.CommandPermissionException;
import Techtony96.Discord.api.commands.exceptions.CommandStateException;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/15/2017.
 */
public class Playlist implements Comparable<Playlist> {

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

    public String getOwnerID(){
        return ownerID;
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

    public void forceAddSong(AudioTrack audioTrack) {
        songs.put(audioTrack.getIdentifier(), audioTrack.getInfo().title);
        PlaylistManager.writePlaylistFile(this);
    }

    public void addSong(IUser requestor, String songID) throws CommandStateException, CommandPermissionException {
        if (!(ownerID.equals(requestor.getStringID()) || contributors.contains(requestor.getStringID())))
            throw new CommandPermissionException("You are not allowed to add songs to " + this.getTitle() + ".");
        if (songs.containsKey(songID))
            throw new CommandStateException("That song is already in this playlist!");
        //songs.put(songID, songID);
        AudioStreamer.getVoiceManager().loadSong(this, songID);
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(IUser requester, String songID) throws CommandStateException, CommandPermissionException {
        if (!(ownerID.equals(requester.getStringID()) || contributors.contains(requester.getStringID())))
            throw new CommandPermissionException("You are not allowed to remove songs from " + this.getTitle() + ".");
        if (!songs.containsKey(songID))
            throw new CommandStateException("That song is not in this playlist!");
        songs.remove(songID);
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(IUser requester, int index) throws CommandPermissionException, CommandStateException {
        if (!(ownerID.equals(requester.getStringID()) || contributors.contains(requester.getStringID())))
            throw new CommandPermissionException("You are not allowed to remove songs from " + this.getTitle() + ".");
        if (index < 0 || index >= songs.size())
            throw new CommandStateException(index + " is not a valid index!");
        songs.remove(getSongIDs().get(index));
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(IUser requester, AudioTrack track) throws CommandStateException, CommandPermissionException {
        if (!(ownerID.equals(requester.getStringID()) || contributors.contains(requester.getStringID())))
            throw new CommandPermissionException("You are not allowed to remove songs from " + this.getTitle() + ".");
        if (!songs.containsValue(track.getInfo().title))
            throw new CommandStateException("That song is not in this playlist!");
        songs.values().remove(track.getInfo().title);
        PlaylistManager.writePlaylistFile(this);
    }

    public void forceRemoveSong(String songID){
        songs.remove(songID);
        PlaylistManager.writePlaylistFile(this);
    }

    public void addContributor(IUser requestor, IUser contributor) throws CommandStateException, CommandPermissionException {
        if (!ownerID.equals(requestor.getStringID()))
            throw new CommandPermissionException("You are not allowed to add contributors to " + this.getTitle() + ".");
        if (contributors.contains(contributor))
            throw new CommandStateException(contributor.getName() + " is already a contributor to " + this.getTitle() + ".");
        contributors.add(contributor.getStringID());
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeContributor(IUser requestor, IUser contributor) throws CommandStateException, CommandPermissionException {
        if (!ownerID.equals(requestor.getStringID()))
            throw new CommandPermissionException("You are not allowed to remove contributors from " + this.getTitle() + ".");
        if (contributors.remove(contributor) == false)
            throw new CommandStateException(contributor.getName() + " is not a contributor to " + this.getTitle() + ".");
        PlaylistManager.writePlaylistFile(this);
    }

    @Override
    public int compareTo(Playlist that) {
        return this.getOwnerID().compareTo(that.getOwnerID());
    }

    public EmbedObject toEmbed(){
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(AudioStreamer.EMBED_COLOR);
        embed.withAuthorName(getTitle());
        embed.withAuthorIcon(getOwner().getAvatarURL());

        embed.withTitle("Songs");
        StringBuilder songs = new StringBuilder();
        int index = 1;
        for (String songID : getSongIDs()) {
            songs.append(index).append(". ").append(getSongTitle(songID).replace("*", " ").replace("_", "").replace("~", "")).append('\n');
            index++;
        }
        songs.setLength(2048);
        embed.withDesc(songs.toString());

        if (getContributors().size() > 0) {
            StringBuilder contributors = new StringBuilder();
            for (IUser c : getContributors()) {
                contributors.append("\n").append(c.getName());
            }
            embed.appendField("Contributors", contributors.toString() + " ", false);
        }
        embed.withFooterText("Playlist by " + getOwner().getName());
        return embed.build();
    }
}
