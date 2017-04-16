package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.modules.audiostreamer.playlists.Playlist;
import Techtony96.Discord.utils.ExceptionMessage;

/**
 * Created by Evan on 4/16/2017.
 */
public class PlaylistCommand {

    private static final String NOPLSELECTED = "No playlist selected\nUse !playlist select *playlist name* to select a playlist";

    /**
     * Looks up and returns a PlayList object that has the name of the passed String
     *
     * @param plName the name of the PlayList that the called wants returned
     * @return the PlayList object corresponding to the passed name
     */
    private static Playlist getPl(String plName) {
        return null;
    }


    /**
     * Has several different instructions concerning playlists
     * -view [PlayListName]
     * Replies with a lists of songs in the playlist
     * -create [PlayListName]
     * creates a new (empty) playlist with the given name
     * The user that calls the method will be made the owner of the playlist
     * The new playlist will automatically become the selected playlist
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
     *
     * @param cc context concerning the issued command
     */
    @BotCommand(command = "playlist", module = "Audio Streamer Module", aliases = "pl", description = "Group of instructions for managing playlists.", usage = "View !pl help for specifics")
    public static void PlaylistCommand(CommandContext cc) {
        if (cc.getArgCount() < 3) {
            cc.replyWith(ExceptionMessage.INCORRECT_USAGE);
            cc.sendUsage();
        }
        String instruction = cc.getArgument(1);
        Playlist current = null;
        if (instruction.equalsIgnoreCase("view")) {
            if (current == null) {
                cc.replyWith(NOPLSELECTED);
                return;
            }


        } else if (instruction.equalsIgnoreCase("create")) {

        } else if (instruction.equalsIgnoreCase("select")) {

        } else if (instruction.equalsIgnoreCase("share")) {

        } else if (instruction.equalsIgnoreCase("unshare")) {

        } else if (instruction.equalsIgnoreCase("add")) {

        } else if (instruction.equalsIgnoreCase("remove")) {

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
