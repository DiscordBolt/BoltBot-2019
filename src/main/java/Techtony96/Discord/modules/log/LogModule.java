package Techtony96.Discord.modules.log;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.event.GuildMemberAddEventResponse;
import sx.blah.discord.api.internal.json.event.GuildMemberRemoveEventResponse;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 12/24/2016.
 */
public class LogModule extends CustomModule implements IModule {

    private IChannel logChannel;

    public LogModule() {
        super("Log", "1.0");
    }

    @EventSubscriber
    public boolean readyEvent(ReadyEvent e) {
        for (IChannel channel : client.getChannels(false)) {
            if (channel.getName().equalsIgnoreCase("bot-log")) {
                logChannel = channel;
                break;
            }
        }
        if (logChannel == null) {
            Logger.error("A logging channel was not found!");
            client.getDispatcher().unregisterListener(this);
        }

        return true;
    }

    @EventSubscriber
    public void onMessageDelete(MessageDeleteEvent e) {
        ChannelUtil.sendMessage(logChannel, formatName(e.getMessage().getAuthor()) + "'s message `" + e.getMessage().getContent() + "` was deleted in " + e.getMessage().getChannel().mention());
    }

    @EventSubscriber
    public void onUserLeave(GuildMemberRemoveEventResponse e) {
        ChannelUtil.sendMessage(logChannel, e.user.username + " left the server.");
    }

    @EventSubscriber
    public void onUserLJoin(GuildMemberAddEventResponse e) {
        ChannelUtil.sendMessage(logChannel, e.user.username + " joined the server.");
    }

    private String formatName(IUser user) {
        return user.getDisplayName(logChannel.getGuild());
    }
}
