package net.ajpappas.discord.modules.audiostreamer.playlists;

import com.discordbolt.api.command.exceptions.CommandPermissionException;
import com.discordbolt.api.command.exceptions.CommandStateException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
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

    private String title;
    private Long ownerID, guildID;
    private List<Long> contributors = new ArrayList<>();
    //private List<String> songIDs = new ArrayList<>();
    private HashMap<String, String> songs = new HashMap<>();

    Playlist(String title, IUser owner, IGuild guild) {
        this.title = title;
        this.ownerID = owner.getLongID();
        this.guildID = guild.getLongID();
    }

    public String getTitle() {
        return title;
    }

    public IUser getOwner() {
        return AudioStreamer.getClient().getUserByID(ownerID);
    }

    public Long getOwnerID() {
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

    public String addSong(IUser requester, String songID) throws CommandStateException, CommandPermissionException {
        if (!(ownerID.equals(requester.getLongID()) || contributors.contains(requester.getLongID())))
            throw new CommandPermissionException("You are not allowed to add songs to " + this.getTitle() + ".");
        if (songs.containsKey(songID))
            throw new CommandStateException("That song is already in this playlist!");

        String songTitle = AudioStreamer.getVoiceManager().getSongTitle(songID);
        songs.put(songID, songTitle);
        PlaylistManager.writePlaylistFile(this);
        return songTitle;
    }

    public void removeSong(IUser requester, String songID) throws CommandStateException, CommandPermissionException {
        if (!(ownerID.equals(requester.getLongID()) || contributors.contains(requester.getLongID())))
            throw new CommandPermissionException("You are not allowed to remove songs from " + this.getTitle() + ".");
        if (!songs.containsKey(songID))
            throw new CommandStateException("That song is not in this playlist!");
        songs.remove(songID);
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(IUser requester, int index) throws CommandPermissionException, CommandStateException {
        if (!(ownerID.equals(requester.getLongID()) || contributors.contains(requester.getLongID())))
            throw new CommandPermissionException("You are not allowed to remove songs from " + this.getTitle() + ".");
        if (index < 0 || index >= songs.size())
            throw new CommandStateException(index + " is not a valid index!");
        songs.remove(getSongIDs().get(index));
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeSong(IUser requester, AudioTrack track) throws CommandStateException, CommandPermissionException {
        if (!(ownerID.equals(requester.getLongID()) || contributors.contains(requester.getLongID())))
            throw new CommandPermissionException("You are not allowed to remove songs from " + this.getTitle() + ".");
        if (!songs.containsValue(track.getInfo().title))
            throw new CommandStateException("That song is not in this playlist!");
        songs.values().remove(track.getInfo().title);
        PlaylistManager.writePlaylistFile(this);
    }

    public void addContributor(IUser requestor, IUser contributor) throws CommandStateException, CommandPermissionException {
        if (!ownerID.equals(requestor.getLongID()))
            throw new CommandPermissionException("You are not allowed to add contributors to " + this.getTitle() + ".");
        if (contributors.contains(contributor.getLongID()))
            throw new CommandStateException(contributor.getName() + " is already a contributor to " + this.getTitle() + ".");
        contributors.add(contributor.getLongID());
        PlaylistManager.writePlaylistFile(this);
    }

    public void removeContributor(IUser requestor, IUser contributor) throws CommandStateException, CommandPermissionException {
        if (!ownerID.equals(requestor.getLongID()))
            throw new CommandPermissionException("You are not allowed to remove contributors from " + this.getTitle() + ".");
        if (contributors.remove(contributor.getLongID()) == false)
            throw new CommandStateException(contributor.getName() + " is not a contributor to " + this.getTitle() + ".");
        PlaylistManager.writePlaylistFile(this);
    }

    @Override
    public int compareTo(Playlist that) {
        return this.getOwnerID().compareTo(that.getOwnerID());
    }

    public EmbedObject toEmbed() {
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
