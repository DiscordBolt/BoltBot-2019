package net.ajpappas.discord.modules.log;

import net.ajpappas.discord.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by Techt on 4/27/2017.
 */
public class LogListener {

    private IGuild guild;
    private IChannel logChannel;

    public LogListener(IDiscordClient client, IGuild guild, IChannel logChannel) {
        this.guild = guild;
        this.logChannel = logChannel;

        client.getDispatcher().registerListener(this);
    }

    public void log(String message) {
        ChannelUtil.sendMessage(logChannel, message);
    }

    @EventSubscriber
    public void onMessageDelete(MessageDeleteEvent e) {
        if (e.getGuild().equals(guild))
            ChannelUtil.sendMessage(logChannel, e.getMessage().getAuthor().getName() + "'s message `" + e.getMessage().getContent() + "` was deleted in " + e.getMessage().getChannel().mention());
    }

    @EventSubscriber
    public void onUserLeave(UserLeaveEvent e) {
        if (e.getGuild().equals(guild))
            ChannelUtil.sendMessage(logChannel, e.getUser().getName() + " left the server.");
    }

    @EventSubscriber
    public void onUserLJoin(UserJoinEvent e) {
        if (e.getGuild().equals(guild))
            ChannelUtil.sendMessage(logChannel, e.getUser().getName() + " joined the server.");
    }
}
