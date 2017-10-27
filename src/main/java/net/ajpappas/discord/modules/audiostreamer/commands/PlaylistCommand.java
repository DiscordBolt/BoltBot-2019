package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandArgumentException;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.api.commands.exceptions.CommandStateException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.modules.audiostreamer.playlists.Playlist;
import net.ajpappas.discord.modules.audiostreamer.playlists.PlaylistManager;
import net.ajpappas.discord.utils.ExceptionMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Created by Evan on 4/16/2017.
 */
public class PlaylistCommand {

    private static final String NO_PL_SELECTED = "No playlist selected\nUse !playlist select *playlist name* to select a playlist.";
    private static final String NO_SUCH_PLAYLIST = "No playlist was found with that name.";

    private static final String VIEW_USAGES = "!Playlist view <playlist>";
    private static final String CREATE_USAGE = "Playlist create [playlist name]";
    private static final String DELETE_USAGE = "!Playlist delete [playlist name]";
    private static final String SELECT_USAGE = "!Playlist select [playlist name/ID]";
    private static final String SHARE_USAGE = "!Playlist share @User";
    private static final String UNSHARE_USAGE = "!Playlist unshare @User";
    private static final String ADD_USAGE = "!Playlist add [Song URL]";
    private static final String REMOVE_USAGE = "!Playlist remove [index]";

    private static PlaylistManager manager;

    private static String getPLNameArgs(CommandContext cc) {
        return cc.combineArgs(2, cc.getArgCount() - 1);
    }

    /**
     * Has several different instructions concerning playlists
     * -view [PlayListName] | view
     * Replies with a lists of songs in the playlist
     * -create [PlayListName]
     * creates a new (empty) playlist with the given name
     * The user that calls the method will be made the owner of the playlist
     * The new playlist will automatically become the selected playlist
     * -delete [PlayListName]
     * deletes an entire playlist with the given name
     * -select [PlayListName]
     * sets the given playlist as "selected" meaning all add, remove, share, unshare will now be in the reference frame of that one playlist
     * -share [@user]
     * gives @user the ability to add songs to and remove songs from the playlist that is selected at the time of the command
     * -unshare [@user]
     * removes @user's ability to add songs to and remove songs from the playlist that is selected at the time of the command
     * -add [Youtube URL]
     * Uses URL link to add audio track to the selected playlist
     * gives song a unique ID number
     * -remove [Song ID]
     * Uses unique ID to remove
     * -help | h
     * replies with usage of all the former instructions
     *
     * @param cc context concerning the issued command
     */
    @BotCommand(command = "playlist", module = "Audio Streamer Module", aliases = "pl", allowPM = true, description = "Group of instructions for managing playlists.", usage = "Playlist help", allowedChannels = "music")
    public static void playlistCommand(CommandContext cc) {
        if (cc.getArgCount() < 2) {
            cc.replyWith(ExceptionMessage.INCORRECT_USAGE);
            cc.sendUsage();
            return;
        }

        manager = AudioStreamer.getPlaylistManager();
        Playlist current = manager.getSelectedPlaylist(cc.getAuthor().getLongID());
        String instruction = cc.getArgument(1);

        if (instruction.equalsIgnoreCase("view")) {
            if (cc.getArgCount() == 2) {
                if (current == null) {
                    cc.replyWith(NO_PL_SELECTED);
                    return;
                }
                cc.replyWith(current.toEmbed());
                return;
            } else {
                Optional<Playlist> temp = manager.getPlaylist(getPLNameArgs(cc));
                if (!temp.isPresent()) {
                    cc.replyWith(NO_SUCH_PLAYLIST);
                    return;
                }
                cc.replyWith(temp.get().toEmbed());
                return;
            }
        } else if (instruction.equalsIgnoreCase("create")) {
            if (cc.getArgCount() < 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "Usage: " + CREATE_USAGE);
                return;
            }
            if (cc.isPrivateMessage()) {
                cc.replyWith(ExceptionMessage.EXECUTE_IN_GUILD);
                return;
            }
            Playlist toCreate;
            try {
                String title = getPLNameArgs(cc);
                for (char c : title.toCharArray()) {
                    if (Character.isLetterOrDigit(c) || c == ' ')
                        continue;
                    cc.replyWith("Playlist titles can only contain alphanumeric characters and spaces.");
                    return;
                }
                toCreate = manager.createPlaylist(title, cc.getAuthor(), cc.getGuild());
            } catch (CommandStateException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            manager.setSelectedPlaylist(cc.getAuthor().getLongID(), toCreate);
            cc.replyWith("Successfully created playlist: " + toCreate.getTitle());
        } else if (instruction.equalsIgnoreCase("delete")) {
            if (cc.isPrivateMessage()) {
                cc.replyWith(ExceptionMessage.EXECUTE_IN_GUILD);
                return;
            }
            if (cc.getArgCount() < 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "Usage: " + DELETE_USAGE);
                return;
            }
            try {
                manager.deletePlaylist(getPLNameArgs(cc), cc.getAuthor());
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            cc.replyWith("Successfully deleted playlist: " + getPLNameArgs(cc));
        } else if (instruction.equalsIgnoreCase("select")) {
            if (cc.getArgCount() < 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "Usage: " + SELECT_USAGE);
                return;
            }
            if (cc.getArgCount() == 3 && cc.getArgument(2).matches("^-?\\d+$")) {
                int playlistIndex = Integer.valueOf(cc.getArgument(2)) - 1;
                try {
                    Playlist toSelect = manager.getPlaylist(playlistIndex);
                    manager.setSelectedPlaylist(cc.getAuthor().getLongID(), toSelect);
                    cc.replyWith("Successfully selected " + toSelect.getTitle() + " by " + toSelect.getOwner().getName() + " as your selected playlist.");
                } catch (CommandArgumentException e) {
                    cc.replyWith(e.getMessage());
                    return;
                }
            } else {
                Optional<Playlist> toSelect = manager.getPlaylist(getPLNameArgs(cc));
                if (!toSelect.isPresent()) {
                    cc.replyWith(NO_SUCH_PLAYLIST);
                    return;
                }
                manager.setSelectedPlaylist(cc.getAuthor().getLongID(), toSelect.get());
                cc.replyWith("Successfully selected " + toSelect.get().getTitle() + " by " + toSelect.get().getOwner().getName() + " as your selected playlist.");
            }
        } else if (instruction.equalsIgnoreCase("share")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "Usage: " + SHARE_USAGE);
                return;
            }
            if (current == null) {
                cc.replyWith(NO_PL_SELECTED);
                return;
            }
            List<IUser> mentions = cc.getMentions();
            if (mentions.size() != 1) {
                cc.replyWith("usage: " + SHARE_USAGE);
                return;
            }
            try {
                current.addContributor(cc.getAuthor(), mentions.get(0));
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            cc.replyWith("Successfully added " + mentions.get(0).getName() + " as a contributor to playlist " + current.getTitle());
        } else if (instruction.equalsIgnoreCase("unshare")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "Usage: " + UNSHARE_USAGE);
                return;
            }
            if (current == null) {
                cc.replyWith(NO_PL_SELECTED);
                return;
            }
            List<IUser> mentions = cc.getMentions();
            if (mentions.size() != 1) {
                cc.replyWith("usage: " + UNSHARE_USAGE);
                return;
            }
            try {
                current.removeContributor(cc.getAuthor(), mentions.get(0));
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            cc.replyWith("Successfully removed " + mentions.get(0).getName() + " from being a contributor to playlist " + current.getTitle());
        } else if (instruction.equalsIgnoreCase("add")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "Usage: " + ADD_USAGE);
                return;
            }
            if (current == null) {
                cc.replyWith(NO_PL_SELECTED);
                return;
            }

            try {
                String title = current.addSong(cc.getAuthor(), cc.getArgument(2));
                cc.replyWith("Added \"" + title + "\" to " + current.getTitle());
                return;
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
                return;
            }
        } else if (instruction.equalsIgnoreCase("remove")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "Usage: " + REMOVE_USAGE);
                return;
            }
            if (current == null) {
                cc.replyWith(NO_PL_SELECTED);
                return;
            }

            String song = cc.getArgument(2);
            try {
                int index = Integer.valueOf(song) - 1;
                current.removeSong(cc.getAuthor(), index);
            } catch (NumberFormatException e) {
                try {
                    current.removeSong(cc.getAuthor(), song);
                } catch (CommandException ex) {
                    cc.replyWith(ex.getMessage());
                    return;
                }
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            cc.replyWith("Successfully removed the song from " + current.getTitle() + ".");
        } else if (instruction.equalsIgnoreCase("list")) {
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
            return;
        } else if (instruction.equalsIgnoreCase("help") || instruction.equalsIgnoreCase("h")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(AudioStreamer.EMBED_COLOR);
            embed.withAuthorName("Playlist Commands");
            embed.withTitle(VIEW_USAGES);
            embed.withDesc("View details of a playlist.");
            embed.appendField(CREATE_USAGE, "Create a new playlist.", false);
            embed.appendField(SELECT_USAGE, "Changes your selected playlist to the one specified.", false);
            embed.appendField(SHARE_USAGE, "Allows the given user to add and remove songs from your selected playlist.", false);
            embed.appendField(UNSHARE_USAGE, "Removes edit permissions of user from selected playlist.", false);
            embed.appendField(ADD_USAGE, "Adds the given song to your selected playlist.", false);
            embed.appendField(REMOVE_USAGE, "Removes the song at the given index from you selected playlist.", false);
            cc.replyWith(embed.build());
            return;
        } else {
            cc.replyWith("Your command was not recognized.\nType !Playlist help for more options.");
        }
    }
}
