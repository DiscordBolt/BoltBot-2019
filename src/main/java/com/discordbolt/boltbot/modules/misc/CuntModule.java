package com.discordbolt.boltbot.modules.misc;

import com.discordbolt.boltbot.api.CustomModule;
import com.discordbolt.boltbot.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 6/23/2017.
 */
public class CuntModule extends CustomModule implements IModule {

    public CuntModule(IDiscordClient client) {
        super(client, "Cunt Module", "1.0");
    }

    @EventSubscriber
    public void onCunt(MessageReceivedEvent e) {
        if (e.getAuthor().isBot())
            return;
        if (e.getMessage().getContent().toLowerCase().startsWith("cunt")) {
            ChannelUtil.sendMessage(e.getChannel(), "cunt");
        }
    }
}


