package Techtony96.Discord.modules.audiostreamer.commands;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.modules.audiostreamer.AudioStreamer;
import Techtony96.Discord.utils.ChannelUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Tony on 4/22/2017.
 */
public class NowPlayingCommand {

    @BotCommand(command = "nowplaying", aliases = {"np", "current", "playing"}, module = "Audio Streamer Module", description = "View what is currently playing", usage = "!NowPlaying")
    public static void nowPlayingCommand(CommandContext cc) {
        AudioTrack at = AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild());
        if (at != null) {
            IMessage nowPlayingMessage = cc.replyWith(AudioStreamer.createPlayingEmbed(cc.getGuild(), AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild())));
            ChannelUtil.addReaction(nowPlayingMessage, new Emoji[]{EmojiManager.getForAlias(":black_right_pointing_double_triangle_with_vertical_bar:"), EmojiManager.getForAlias(":star:")});
            AudioStreamer.getVoiceManager().putNowPlayingMessage(nowPlayingMessage, AudioStreamer.getVoiceManager().getNowPlaying(cc.getGuild()));
            return;
        } else {
            cc.replyWith("Nothing is currently playing. Play something with !Play");
            return;
        }
    }
}
