package net.ajpappas.discord.modules.disconnect;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.modules.log.LogModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.modules.IModule;

import java.util.List;

/**
 * Created by Tony Pappas on 12/2/2016.
 */
public class DisconnectModule extends CustomModule implements IModule {

    public DisconnectModule(IDiscordClient client) {
        super(client, "Disconnect Module", "1.1");
    }

    @BotCommand(command = "disconnect", module = "Disconnect Module", aliases = "dis", description = "Disconnect user(s) from their voice channel.", usage = "Disconnect @User1 @User2", permissions = Permissions.VOICE_MOVE_MEMBERS)
    public static void disconnectCommand(CommandContext cc) {
        List<IUser> mentions = cc.getMentions();
        if (mentions.size() < 1) {
            cc.sendUsage();
            return;
        }
        boolean createChannel = false;
        for (IUser u : mentions) {
            if (u.getVoiceStateForGuild(cc.getGuild()).getChannel() != null) {
                createChannel = true;
                break;
            }
        }
        if (!createChannel) {
            cc.replyWith(cc.getUserDisplayName() + ", none of the users specified are connected to a voice channel.");
            return;
        }

        IVoiceChannel temp = cc.getGuild().createVoiceChannel("Disconnect");
        for (IUser u : mentions) {
            if (u.getVoiceStateForGuild(cc.getGuild()).getChannel() == null)
                continue;
            u.moveToVoiceChannel(temp);
        }
        temp.delete();
        cc.replyWith(cc.getUserDisplayName() + ", successfully removed users from voice channels.");
        StringBuilder sb = new StringBuilder();

        sb.append(mentions.get(0).getName());
        for (int i = 1; i < mentions.size(); i++)
            sb.append(", " + mentions.get(i).getName());
        LogModule.logMessage(cc.getGuild(), cc.getUser() + " just disconnected: " + sb.toString());
    }
}
