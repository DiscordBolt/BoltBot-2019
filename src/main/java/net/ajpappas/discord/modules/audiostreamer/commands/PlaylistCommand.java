package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandArgumentException;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.api.commands.exceptions.CommandStateException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.modules.audiostreamer.playlists.Playlist;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Created by Evan on 4/16/2017.
 * Rewritten by Tony on 10/29/2017.
 */
public class PlaylistCommand {

    private static final String NO_PLAYLIST_SELECTED = "You do not have a selected playlist.";
    private static final String NO_SUCH_PLAYLIST = "No playlist was found with that name.";

    @BotCommand(command = {"playlist", "view"}, module = AudioStreamer.MODULE, aliases = "pl", allowPM = true, description = "View a playlist's details", usage = "Playlist view {name}", allowedChannels = "music", minArgs = 2, maxArgs = 100)
    public static void playlistViewCommand(CommandContext cc) throws CommandException {
        if (cc.getArgCount() == 2) {
            Playlist playlist = AudioStreamer.getPlaylistManager().getSelectedPlaylist(cc.getAuthor().getLongID());
            if (playlist == null)
                throw new CommandStateException(NO_PLAYLIST_SELECTED);

            cc.replyWith(playlist.toEmbed());
        } else {
            Optional<Playlist> playlist = AudioStreamer.getPlaylistManager().getPlaylist(getPlaylistName(cc));
            if (!playlist.isPresent())
                throw new CommandArgumentException(NO_SUCH_PLAYLIST);

            cc.replyWith(playlist.get().toEmbed());
        }
    }

    @BotCommand(command = {"playlist", "create"}, module = AudioStreamer.MODULE, aliases = "pl", description = "Create a new playlist", usage = "Playlist create [name]", allowedChannels = "music", minArgs = 3, maxArgs = 100)
    public static void playlistCreateCommand(CommandContext cc) throws CommandException {
        String title = getPlaylistName(cc);
        if (title.length() > 250)
            throw new CommandArgumentException("Playlist titles can only be 250 characters long.");
        if (!title.matches("[a-zA-Z0-9 ]+"))
            throw new CommandArgumentException("Playlist titles can only contain alphanumeric characters and spaces.");

        Playlist playlist = AudioStreamer.getPlaylistManager().createPlaylist(title, cc.getAuthor(), cc.getGuild());
        AudioStreamer.getPlaylistManager().setSelectedPlaylist(cc.getAuthor().getLongID(), playlist);
        cc.replyWith("Successfully created playlist: " + playlist.getTitle());
    }

    @BotCommand(command = {"playlist", "delete"}, module = AudioStreamer.MODULE, aliases = "pl", description = "Delete a playlist", usage = "Playlist delete [name]", allowedChannels = "music", minArgs = 3, maxArgs = 100)
    public static void playlistDeleteCommand(CommandContext cc) throws CommandException {
        AudioStreamer.getPlaylistManager().deletePlaylist(getPlaylistName(cc), cc.getAuthor());
        cc.replyWith("Successfully deleted playlist: " + getPlaylistName(cc));
    }

    @BotCommand(command = {"playlist", "select"}, module = AudioStreamer.MODULE, aliases = "pl", allowPM = true, description = "Select a playlist", usage = "Playlist select [number/name]", allowedChannels = "music", minArgs = 3, maxArgs = 100)
    public static void playlistSelectCommand(CommandContext cc) throws CommandException {
        if (cc.getArgCount() == 3 && cc.getArgument(2).matches("^-?\\d+$")) {
            int playlistIndex = Integer.valueOf(cc.getArgument(2)) - 1;
            Playlist playlist = AudioStreamer.getPlaylistManager().getPlaylist(playlistIndex);
            AudioStreamer.getPlaylistManager().setSelectedPlaylist(cc.getAuthor().getLongID(), playlist);
            cc.replyWith("Successfully selected " + playlist.getTitle() + " by " + playlist.getOwner().getName() + " as your selected playlist.");
        } else {
            Optional<Playlist> playlist = AudioStreamer.getPlaylistManager().getPlaylist(getPlaylistName(cc));
            if (!playlist.isPresent())
                throw new CommandArgumentException(NO_SUCH_PLAYLIST);

            AudioStreamer.getPlaylistManager().setSelectedPlaylist(cc.getAuthor().getLongID(), playlist.get());
            cc.replyWith("Successfully selected " + playlist.get().getTitle() + " by " + playlist.get().getOwner().getName() + " as your selected playlist.");
        }
    }

    @BotCommand(command = {"playlist", "share"}, module = AudioStreamer.MODULE, aliases = "pl", allowPM = true, description = "Give another user access to add/remove songs", usage = "Playlist share [@User]", allowedChannels = "music", args = 3)
    public static void playlistShareCommand(CommandContext cc) throws CommandException {
        Playlist selectedPlaylist = AudioStreamer.getPlaylistManager().getSelectedPlaylist(cc.getAuthor().getLongID());
        if (selectedPlaylist == null)
            throw new CommandStateException(NO_PLAYLIST_SELECTED);
        if (cc.getMessage().getMentions().size() != 1) {
            cc.sendUsage();
            return;
        }

        selectedPlaylist.addContributor(cc.getAuthor(), cc.getMessage().getMentions().get(0));
        cc.replyWith("Successfully added " + cc.getMessage().getMentions().get(0).getName() + " as a contributor to playlist " + selectedPlaylist.getTitle());
    }

    @BotCommand(command = {"playlist", "unshare"}, module = AudioStreamer.MODULE, aliases = "pl", allowPM = true, description = "Remove another user's access to add/remove songs", usage = "Playlist unshare [@User]", allowedChannels = "music", args = 3)
    public static void playlistUnshareCommand(CommandContext cc) throws CommandException {
        Playlist selectedPlaylist = AudioStreamer.getPlaylistManager().getSelectedPlaylist(cc.getAuthor().getLongID());
        if (selectedPlaylist == null)
            throw new CommandStateException(NO_PLAYLIST_SELECTED);
        if (cc.getMessage().getMentions().size() != 1) {
            cc.sendUsage();
            return;
        }

        selectedPlaylist.removeContributor(cc.getAuthor(), cc.getMessage().getMentions().get(0));
        cc.replyWith("Successfully removed " + cc.getMessage().getMentions().get(0).getName() + " from being a contributor to playlist " + selectedPlaylist.getTitle());
    }

    @BotCommand(command = {"playlist", "add"}, module = AudioStreamer.MODULE, aliases = "pl", allowPM = true, description = "Add a song to your selected playlist", usage = "Playlist add [URL]", allowedChannels = "music", args = 3)
    public static void playlistAddCommand(CommandContext cc) throws CommandException {
        Playlist selectedPlaylist = AudioStreamer.getPlaylistManager().getSelectedPlaylist(cc.getAuthor().getLongID());
        if (selectedPlaylist == null)
            throw new CommandStateException(NO_PLAYLIST_SELECTED);

        String title = selectedPlaylist.addSong(cc.getAuthor(), cc.getArgument(2));
        cc.replyWith("Added \"" + title + "\" to " + selectedPlaylist.getTitle());
    }

    @BotCommand(command = {"playlist", "remove"}, module = AudioStreamer.MODULE, aliases = "pl", allowPM = true, description = "Remove a song from your selected playlist", usage = "Playlist remove [number/URL]", allowedChannels = "music", args = 3)
    public static void playlistRemoveCommand(CommandContext cc) throws CommandException {
        Playlist selectedPlaylist = AudioStreamer.getPlaylistManager().getSelectedPlaylist(cc.getAuthor().getLongID());
        if (selectedPlaylist == null)
            throw new CommandStateException(NO_PLAYLIST_SELECTED);

        if (cc.getArgument(2).matches("^-?\\d+$")) {
            selectedPlaylist.removeSong(cc.getAuthor(), Integer.valueOf(cc.getArgument(2)) - 1);
        } else {
            selectedPlaylist.removeSong(cc.getAuthor(), cc.getArgument(2));
        }
        cc.replyWith("Successfully removed the song from " + selectedPlaylist.getTitle() + ".");
    }

    @BotCommand(command = {"playlist", "list"}, module = AudioStreamer.MODULE, aliases = "pl", allowPM = true, description = "List all stored playlists", usage = "Playlist list", allowedChannels = "music", args = 2)
    public static void playlistListCommand(CommandContext cc) throws CommandException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(AudioStreamer.EMBED_COLOR);
        embed.withAuthorName("Playlists");
        List<Playlist> playlists = AudioStreamer.getPlaylistManager().getPlaylists();
        embed.withTitle("Number of Playlists:");
        embed.withDescription(playlists.size() + "");

        IUser currentUser = null;
        StringBuilder sb = new StringBuilder();
        int id = 1;
        for (Playlist pl : playlists) {
            if (currentUser == null || !pl.getOwnerID().equals(currentUser.getLongID())) {
                if (currentUser != null)
                    embed.appendField(currentUser.getName(), sb.toString(), false);
                sb.setLength(0);
                currentUser = pl.getOwner();
            }
            if (sb.length() != 0)
                sb.append('\n');
            sb.append(id++).append(". ").append(pl.getTitle());
        }
        if (currentUser != null)
            embed.appendField(currentUser.getName(), sb.toString(), false);
        cc.replyWith(embed.build());
    }

    private static String getPlaylistName(CommandContext cc) {
        return cc.combineArgs(2, cc.getArgCount() - 1);
    }
}
