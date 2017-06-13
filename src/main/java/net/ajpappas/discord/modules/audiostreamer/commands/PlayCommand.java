package net.ajpappas.discord.modules.audiostreamer.commands;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.api.commands.exceptions.CommandPermissionException;
import net.ajpappas.discord.api.commands.exceptions.CommandStateException;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.modules.audiostreamer.playlists.Playlist;
import net.ajpappas.discord.modules.audiostreamer.playlists.PlaylistManager;
import net.ajpappas.discord.modules.audiostreamer.voice.VoiceManager;
import net.ajpappas.discord.utils.ExceptionMessage;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Created by Evan on 4/16/2017.
 */
public class PlayCommand {

    private static final String PLAY_SONG = "!Play [URL]";
    private static final String PLAY_PLAYLIST = "!Play playlist <playlist name>";
    private static final String PLAY_RANDOM = "!Play random";

    @BotCommand(command = "play", module = "Audio Streamer Module", description = "Instruct the bot to start playing something.", usage = "Play help", allowedChannels = "music")
    public static void playCommand(CommandContext cc) {
        if (cc.getArgCount() == 1 && AudioStreamer.getVoiceManager().isPaused(cc.getGuild())) {
            try {
                AudioStreamer.getVoiceManager().unpause(cc.getGuild(), cc.getUser());
                return;
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
                return;
            }
        }

        if (cc.getArgCount() < 2) {
            cc.replyWith(ExceptionMessage.INCORRECT_USAGE);
            cc.sendUsage();
            return;
        }

        PlaylistManager playlistManager = AudioStreamer.getPlaylistManager();
        VoiceManager voiceManager = AudioStreamer.getVoiceManager();
        Playlist current = playlistManager.getSelectedPlaylist(cc.getUser().getLongID());
        String instruction = cc.getArgument(1);

        if (instruction.equalsIgnoreCase("playlist") || instruction.equalsIgnoreCase("-p")) {
            Playlist toPlay = null;
            if (cc.getArgCount() == 2) {
                toPlay = current;
            } else if (cc.getArgCount() > 2) {
                String playlistRequest = cc.combineArgs(2, cc.getArgCount() - 1);
                if (playlistRequest.contains(":")) {
                    String playlistTitle = playlistRequest.split(":")[0];
                    int songNumber = -1;
                    try {
                        songNumber = Integer.valueOf(playlistRequest.split(":")[1]);
                    } catch (NumberFormatException e) {
                        cc.replyWith("You have not specified a valid song number!");
                        return;
                    }
                    toPlay = playlistManager.getPlaylist(playlistTitle).orElse(null);
                    if (toPlay == null) {
                        cc.replyWith("You do not have a selected playlist!");
                        return;
                    }
                    if (songNumber < 1 || songNumber > toPlay.getSongIDs().size()) {
                        cc.replyWith("You have not specified a valid song number!");
                        return;
                    }

                    try {
                        voiceManager.queue(cc.getGuild(), cc.getUser(), toPlay.getSongIDs().get(songNumber - 1));
                        //cc.replyWith("Successfully queued song #" + songNumber + " from " + toPlay.getTitle());
                        return;
                    } catch (CommandPermissionException e) {
                        cc.replyWith(e.getMessage());
                        return;
                    } catch (CommandStateException e) {
                        cc.replyWith(e.getMessage());
                        return;
                    }
                } else {
                    toPlay = playlistManager.getPlaylist(playlistRequest).orElse(null);
                    try {
                        voiceManager.queue(cc.getGuild(), cc.getUser(), toPlay);
                        if (toPlay.getSongIDs().size() > 5) {
                            cc.replyWith("Your playlist is now being queued and may take ~30 seconds to fully appear in the queue.");
                            return;
                        } else {
                            cc.replyWith("Your playlist is now being queued.");
                            return;
                        }
                    } catch (CommandException e) {
                        cc.replyWith(e.getMessage());
                        return;
                    }
                }
            }
        } else if (instruction.equalsIgnoreCase("random")) {
            voiceManager.playRandom(cc.getGuild(), cc.getUser());
            if (voiceManager.isPlayingRandom(cc.getGuild())) {
                cc.replyWith("I will now continuously queue up random songs!");
                return;
            } else {
                cc.replyWith("I will stop playing random songs.");
                return;
            }
        } else if (instruction.equalsIgnoreCase("help")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(AudioStreamer.EMBED_COLOR);
            embed.withAuthorName("Play Commands");
            embed.withTitle(PLAY_SONG);
            embed.withDesc("Play the song from a given URL.");
            embed.appendField(PLAY_PLAYLIST, "Play all songs on a given playlist.", false);
            embed.appendField(PLAY_RANDOM, "Play a random song.", false);
            cc.replyWith(embed.build());
            return;
        } else {
            try {
                voiceManager.queue(cc.getGuild(), cc.getUser(), cc.getArgument(1));
                cc.replyWith("Your song is now being queued up");
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
            }
            return;
        }
    }
}
