package com.discordbolt.boltbot.modules.disconnect;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.api.command.exceptions.CommandStateException;
import com.discordbolt.boltbot.system.CustomModule;
import com.discordbolt.boltbot.utils.UserUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony Pappas on 12/2/2016.
 */
public class DisconnectModule extends CustomModule implements IModule {

    public DisconnectModule(IDiscordClient client) {
        super(client, "Disconnect Module", "1.1");
    }

    @BotCommand(command = "disconnect", module = "Disconnect Module", description = "Disconnect user(s) from their voice channel.", usage = "Disconnect [User] ", permissions = Permissions.VOICE_MOVE_MEMBERS)
    public static void disconnectCommand(CommandContext cc) throws CommandException {
        IUser user = UserUtil.findUser(cc.getMessage(), 12);

        if (user == null)
            throw new CommandArgumentException("The user you specified was unable to be found!");
        if (user.getVoiceStateForGuild(cc.getGuild()).getChannel() == null)
            throw new CommandStateException("The user you specified is not connected to a voice channel!");

        IVoiceChannel temp = cc.getGuild().createVoiceChannel("Disconnect");
        user.moveToVoiceChannel(temp);
        temp.delete();
        cc.getMessage().delete();
    }
}
