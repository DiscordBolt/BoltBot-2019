package com.discordbolt.boltbot.discord.modules.misc;

import com.discordbolt.boltbot.discord.api.BotModule;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class CuntModule implements BotModule {

    @Override
    public void initialize(DiscordClient client) {
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .filter(e -> e.getMember().isPresent())
                .filter(e -> !e.getMember().get().isBot())
                .filter(e -> e.getMessage().getContent().isPresent())
                .filter(e -> e.getMessage().getContent().get().toLowerCase().startsWith("cunt ") || e.getMessage().getContent().get().equalsIgnoreCase("cunt"))
                .flatMap(e -> e.getMessage().getChannel())
                .flatMap(channel -> channel.createMessage("Cunt"))
                .subscribe();
    }
}
