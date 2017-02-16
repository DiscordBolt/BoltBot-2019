package Techtony96.Discord.modules.disconnect;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.ExceptionMessage;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Tony Pappas on 12/2/2016.
 */
public class DisconnectModule extends CustomModule implements IModule {

    public DisconnectModule() {
        super("Disconnect", "1.1");
    }

    @EventSubscriber
    public void OnMesageEvent(MessageReceivedEvent e) {
        IMessage message = e.getMessage();
        IUser user = e.getMessage().getAuthor();
        IChannel channel = message.getChannel();

        if (message.getContent().split(" ")[0].toLowerCase().equals("!disconnect")) {
            for (IRole role : user.getRolesForGuild(message.getGuild())) {
                if (role.getPermissions().contains(Permissions.VOICE_MOVE_MEMBERS)) {
                    break;
                }
                ChannelUtil.sendMessage(channel, user.mention() + " " + ExceptionMessage.PERMISSION_DENIED);
                return;
            }

            if (message.getMentions().size() < 1) {
                ChannelUtil.sendMessage(channel, user.mention() + " Incorrect arguments. Usage: !Disconnect [@User]");
                return;
            }
            try {
                boolean createChannel = false;
                for (IUser u : message.getMentions()) {
                    if (u.getConnectedVoiceChannels().size() > 0) {
                        createChannel = true;
                        break;
                    }
                }
                if (!createChannel) {
                    ChannelUtil.sendMessage(channel, user.mention() + " None of the users specified are connected to a voice channel.");
                    return;
                }

                IVoiceChannel temp = message.getGuild().createVoiceChannel("Disconnect");
                for (IUser u : message.getMentions()) {
                    if (u.getConnectedVoiceChannels().size() < 0)
                        continue;
                    u.moveToVoiceChannel(temp);
                }
                temp.delete();
                ChannelUtil.sendMessage(channel, user.mention() + " Successfully removed users from voice channels.");
            } catch (RateLimitException ex) {
                Logger.error(ExceptionMessage.API_LIMIT);
                Logger.debug(ex);
            } catch (DiscordException ex) {
                Logger.error("Discord Exception: " + ex.getErrorMessage());
                Logger.debug(ex);
            } catch (MissingPermissionsException ex) {
                Logger.error("Unable to delete channel " + channel.getName() + ". Missing Permissions.");
                Logger.debug(ex);
            }
        }
    }
}
