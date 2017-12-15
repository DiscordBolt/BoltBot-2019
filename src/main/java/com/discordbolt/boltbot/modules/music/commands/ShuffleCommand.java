package com.discordbolt.boltbot.modules.music.commands;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.modules.music.MusicModule;

/**
 * Created by Tony on 4/26/2017.
 */
public class ShuffleCommand {

    @BotCommand(command = "shuffle", description = "Shuffle the current queue", usage = "Shuffle", module = MusicModule.MODULE, allowedChannels = "music")
    public static void shuffleCommand(CommandContext cc) throws CommandException {
        MusicModule.getVoiceManager().shuffle(cc.getGuild(), cc.getAuthor());
    }
}
