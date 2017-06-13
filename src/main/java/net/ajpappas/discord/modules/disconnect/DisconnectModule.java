package net.ajpappas.discord.modules.disconnect;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.modules.log.LogModule;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony Pappas on 12/2/2016.
 */
public class DisconnectModule extends CustomModule implements IModule {

    public DisconnectModule() {
        super("Disconnect Module", "1.1");
    }

    @BotCommand(command = "disconnect", module = "Disconnect Module", aliases = "dis", description = "Disconnect user(s) from their voice channel.", usage = "Disconnect @User1 @User2", permissions = Permissions.VOICE_MOVE_MEMBERS)
    public static void disconnectCommand(CommandContext cc) {
        if (cc.getMessage().getMentions().size() < 1) {
            cc.sendUsage();
            return;
        }
        boolean createChannel = false;
        for (IUser u : cc.getMentions()) {
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
        for (IUser u : cc.getMentions()) {
            if (u.getVoiceStateForGuild(cc.getGuild()).getChannel() == null)
                continue;
            u.moveToVoiceChannel(temp);
        }
        temp.delete();
        cc.replyWith(cc.getUserDisplayName() + ", successfully removed users from voice channels.");
        StringBuilder sb = new StringBuilder();
        cc.getMentions().forEach(u -> sb.append(u.getName() + ", "));
        LogModule.logMessage(cc.getGuild(), cc.getUser() + " just disconnected: " + sb.toString());
    }
}
