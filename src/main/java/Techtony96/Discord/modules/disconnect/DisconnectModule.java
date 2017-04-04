package Techtony96.Discord.modules.disconnect;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony Pappas on 12/2/2016.
 */
public class DisconnectModule extends CustomModule implements IModule {

    public DisconnectModule() {
        super("Disconnect", "1.1");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e){
        new BotCommand(client, "disconnect"){
            @Override
            public void execute(CommandContext cc) {
                if (cc.getMessage().getMentions().size() < 1){
                    sendUsage(cc, true);
                    return;
                }
                boolean createChannel = false;
                for (IUser u : cc.getMentions()) {
                    if (u.getConnectedVoiceChannels().size() > 0) {
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
                    if (u.getConnectedVoiceChannels().size() < 0)
                        continue;
                    u.moveToVoiceChannel(temp);
                }
                temp.delete();
                cc.replyWith(cc.getUserDisplayName() + ", successfully removed users from voice channels.");
            }
        }.setAliases("dis").setPermissions(Permissions.VOICE_MOVE_MEMBERS).setUsage("!Disconnect @User1 @User2").setDescription("Disconnect user(s) from their voice channel.");
    }
}
