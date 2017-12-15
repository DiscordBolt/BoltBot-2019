package com.discordbolt.boltbot.modules.music.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.api.command.exceptions.CommandStateException;
import com.discordbolt.boltbot.modules.music.MusicModule;
import com.discordbolt.boltbot.utils.ChannelUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Tony on 4/22/2017.
 */
public class NowPlayingCommand {

    @BotCommand(command = "nowplaying", aliases = {"np", "current", "playing"}, module = MusicModule.MODULE, description = "View what is currently playing", usage = "NowPlaying", allowedChannels = "music")
    public static void nowPlayingCommand(CommandContext cc) throws CommandException {
        AudioTrack at = MusicModule.getVoiceManager().getNowPlaying(cc.getGuild());
        if (at == null)
            throw new CommandStateException("Nothing is currently playing. Play something with !Play");

        IMessage nowPlayingMessage = cc.replyWith(MusicModule.createPlayingEmbed(cc.getGuild(), MusicModule.getVoiceManager().getNowPlaying(cc.getGuild())));
        ChannelUtil.addReaction(nowPlayingMessage, new Emoji[]{EmojiManager.getForAlias(":black_right_pointing_double_triangle_with_vertical_bar:"), EmojiManager.getForAlias(":star:")});
        MusicModule.getVoiceManager().putNowPlayingMessage(nowPlayingMessage, MusicModule.getVoiceManager().getNowPlaying(cc.getGuild()));
    }
}
