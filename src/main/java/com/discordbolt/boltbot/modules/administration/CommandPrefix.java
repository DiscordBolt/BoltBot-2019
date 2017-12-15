package com.discordbolt.boltbot.modules.administration;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.boltbot.BoltBot;
import com.discordbolt.boltbot.api.mysql.data.persistent.GuildData;
import sx.blah.discord.handle.obj.Permissions;

public class CommandPrefix {

    @BotCommand(command = "prefix", description = "Change the command prefix", usage = "Prefix [Character]", module = "Administration", args = 2, permissions = Permissions.ADMINISTRATOR)
    public static void setCommandPrefix(CommandContext cc) {
        GuildData.getById(cc.getGuild().getLongID()).ifPresent(g -> g.setCommandPrefix(cc.getArgument(1).charAt(0) + ""));
        BoltBot.getCommandManager().setCommandPrefix(cc.getGuild(), cc.getArgument(1).charAt(0));
        cc.replyWith("Your new custom prefix is `" + BoltBot.getCommandManager().getCommandPrefix(cc.getGuild()) + "`. All commands must start with your new prefix.");
    }
}
