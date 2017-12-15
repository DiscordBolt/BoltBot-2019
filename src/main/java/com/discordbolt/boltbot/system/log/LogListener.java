package com.discordbolt.boltbot.system.log;

import com.discordbolt.boltbot.utils.ChannelUtil;
import com.discordbolt.boltbot.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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
        if (e.getGuild().equals(guild)){
            String message = e.getMessage().getAuthor().getName() + "'s message `" + e.getMessage().getContent() + "` was deleted in " + e.getMessage().getChannel().mention();

            if (e.getMessage().getAttachments().isEmpty()){
                ChannelUtil.sendMessage(logChannel, message);
            } else {
                IMessage.Attachment attachment = e.getMessage().getAttachments().get(0);
                try {
                    URLConnection url = new URL(attachment.getUrl()).openConnection();
                    url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
                    ChannelUtil.sendFile(logChannel, message, attachment.getFilename(), url.getInputStream());
                } catch (IOException ex) {
                    Logger.error("Unable to parse " + e.getMessage().getAuthor().getName() + "'s uploaded file ");
                    Logger.debug("User: " + e.getMessage().getAuthor().getName() + " File: " + attachment.getFilename() + " URL: " + attachment.getUrl());
                    Logger.debug(ex);
                }
            }


            // Get attachments
            if (!e.getMessage().getAttachments().isEmpty()){
                List<IMessage.Attachment> attachments = e.getMessage().getAttachments();

                for (IMessage.Attachment attachment : attachments){

                }
            }
        }
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
