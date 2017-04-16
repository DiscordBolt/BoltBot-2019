package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.modules.audiostreamer.playlists.Playlist;
import Techtony96.Discord.modules.audiostreamer.playlists.PlaylistManager;
import Techtony96.Discord.modules.audiostreamer.songs.Song;
import Techtony96.Discord.utils.ExceptionMessage;
import sun.security.provider.SHA;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Evan on 4/16/2017.
 */
public class PlaylistCommand {

    private static final String NO_PL_SELECTED = "No playlist selected\nUse !playlist select *playlist name* to select a playlist.";
    private static final String NO_SUCH_PLAYLIST = "No playlist was found with that name.";

    private static final String VIEW_USAGE = "!playlist view *playlist name* **OR** !playlist view.";
    private static final String CREATE_USAGE = "!playlist create *playlist name*.";
    private static final String SELECT_USAGE = "!playlist select *playlist name*.";
    private static final String SHARE_USAGE = "!playlist share *@user*.";
    private static final String UNSHARE_USAGE = "!playlist unshare *@user*.";
    private static final String ADD_USAGE = "!playlist add *YoutubeURL*.";
    private static final String REMOVE_USAGE = "!playlist remove *index*.";

    private static HashMap<String, Playlist> selectedPlaylist = new HashMap<>();
    private static PlaylistManager manager;

    private static String getPLNameArgs(CommandContext cc) {
        return cc.combineArgs(2, cc.getArgCount() - 1);
    }
    /**
     * Has several different instructions concerning playlists
     * -view [PlayListName] | view
     *      Replies with a lists of songs in the playlist
     * -create [PlayListName]
     *      creates a new (empty) playlist with the given name
     *      The user that calls the method will be made the owner of the playlist
     *      The new playlist will automatically become the selected playlist
     * -select [PlayListName]
     *      sets the given playlist as "selected" meaning all add, remove, share, unshare will now be in the reference frame of that one playlist
     * -share [@user]
     *      gives @user the ability to add songs to and remove songs from the playlist that is selected at the time of the command
     * -unshare [@user]
     *      removes @user's ability to add songs to and remove songs from the playlist that is selected at the time of the command
     * -add [Youtube URL]
     *      Uses URL link to add audio track to the selected playlist
     *      gives song a unique ID number
     * -remove [Song ID]
     *      Uses unique ID to remove
     * -help | h
     *      replies with usage of all the former instructions
     *
     * @param cc context concerning the issued command
     */
    @BotCommand(command = "playlist", module = "Audio Streamer Module", aliases = "pl", description = "Group of instructions for managing playlists.", usage = "View !playlist help for specifications")
    public static void PlaylistCommand(CommandContext cc) {
        if (cc.getArgCount() < 2) {
            cc.replyWith(ExceptionMessage.INCORRECT_USAGE);
            cc.sendUsage();
        }

        manager = AudioStreamer.getPlaylistManager();
        Playlist current = selectedPlaylist.get(cc.getUser().getID());
        String instruction = cc.getArgument(1);

        if (instruction.equalsIgnoreCase("view")) {
            Playlist toPrint;
            if (cc.getArgCount() == 2) {
                if (current == null) {
                    cc.replyWith(NO_PL_SELECTED);
                    return;
                }
                toPrint = current;
            } else {
                Optional<Playlist> temp = manager.getPlaylist(getPLNameArgs(cc));
                if (!temp.isPresent()) {
                    cc.replyWith(NO_SUCH_PLAYLIST);
                    return;
                }
                toPrint = temp.get();
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(AudioStreamer.EMBED_COLOR);
            embed.withAuthorName(toPrint.getTitle());
            embed.withAuthorIcon(toPrint.getOwner().getAvatarURL());

            embed.withTitle("Songs");
            StringBuilder songs = new StringBuilder();
            int index = 1;
            for (Song s : toPrint.getSongs()) {
                songs.append(index).append(". ").append(s.getTitle()).append('\n');
                index++;
            }
            embed.withDesc(songs.toString());

            StringBuilder contributors = new StringBuilder();
            contributors.append("Contributors:");
            for (IUser c : toPrint.getContributors()) {
                contributors.append("\n  ").append(c.getName());
            }
            embed.withFooterText("Playlist by " + toPrint.getOwner().getName() + "\n" + contributors.toString());
            cc.replyWith(embed.build());

        } else if (instruction.equalsIgnoreCase("create")) {
            if (cc.getArgCount() < 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "usage: " + CREATE_USAGE);
                return;
            }
            Playlist toCreate = manager.createPlaylist(getPLNameArgs(cc), cc.getUser(), cc.getGuild());
            selectedPlaylist.put(cc.getUser().getID(), toCreate);
            cc.replyWith("Successfully created playlist: " + toCreate.getTitle());
        } else if (instruction.equalsIgnoreCase("select")) {
            if (cc.getArgCount() < 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "usage: " + SELECT_USAGE);
                return;
            }
            Optional<Playlist> toSelect = manager.getPlaylist(getPLNameArgs(cc));
            if (!toSelect.isPresent()) {
                cc.replyWith(NO_SUCH_PLAYLIST);
                return;
            }
            selectedPlaylist.put(cc.getUser().getID(), toSelect.get());
            cc.replyWith("Successfully selected " + toSelect.get() + " as your selected playlist.");
        } else if (instruction.equalsIgnoreCase("share")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "usage: " + SHARE_USAGE);
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
                current.addContributor(mentions.get(0));
            } catch (IllegalArgumentException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            cc.replyWith("Successfully added " + mentions.get(0).getName() + " as a contributor to playlist " + current.getTitle());

        } else if (instruction.equalsIgnoreCase("unshare")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "usage: " + UNSHARE_USAGE);
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
                current.removeContributor(mentions.get(0));
            } catch (IllegalArgumentException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            cc.replyWith("Successfully removed " + mentions.get(0).getName() + " from being a contributor to playlist " + current.getTitle());
        } else if (instruction.equalsIgnoreCase("add")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "usage: " + ADD_USAGE);
                return;
            }
            Song toAdd;
            try {
                toAdd = current.addSong(AudioStreamer.getSongManager().createSong(cc.getArgument(2)));
            } catch (IllegalArgumentException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            if (toAdd == null) {
                cc.replyWith("Problem adding song from " + cc.getArgument(2) + ".");
            }
            cc.replyWith("Successfully added " + toAdd.getTitle() + " to " + current.getTitle() + ".");
        } else if (instruction.equalsIgnoreCase("remove")) {
            if (cc.getArgCount() != 3) {
                cc.replyWith(ExceptionMessage.INCORRECT_USAGE + "\n" + "usage: " + REMOVE_USAGE);
                return;
            }
            Song toRemove;
            try {
                toRemove = current.removeSong(Integer.valueOf(cc.getArgument(2)));
            } catch (IllegalArgumentException e) {
                cc.replyWith(e.getMessage());
                return;
            }
            if (toRemove == null) {
                cc.replyWith("Problem removing song from " + cc.getArgument(2) + ".");
            }
            cc.replyWith("Successfully removed " + toRemove.getTitle() + " to " + current.getTitle() + ".");
        } else if (instruction.equalsIgnoreCase("help") || instruction.equalsIgnoreCase("h")) {

        }

//        if (cc.getMessage().getMentions().size() < 1) {
//            cc.sendUsage();
//            return;
//        }
//        boolean createChannel = false;
//        for (IUser u : cc.getMentions()) {
//            if (u.getVoiceStateForGuild(cc.getGuild()).getChannel() != null) {
//                createChannel = true;
//                break;
//            }
//        }
//        if (!createChannel) {
//            cc.replyWith(cc.getUserDisplayName() + ", none of the users specified are connected to a voice channel.");
//            return;
//        }
//
//        IVoiceChannel temp = cc.getGuild().createVoiceChannel("Disconnect");
//        for (IUser u : cc.getMentions()) {
//            if (u.getVoiceStateForGuild(cc.getGuild()).getChannel() == null)
//                continue;
//            u.moveToVoiceChannel(temp);
//        }
//        temp.delete();
//        cc.replyWith(cc.getUserDisplayName() + ", successfully removed users from voice channels.");
    }
}
