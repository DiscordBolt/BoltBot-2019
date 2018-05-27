package com.discordbolt.boltbot.discord;

import discord4j.core.ClientBuilder;
import discord4j.core.DiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DiscordConfiguration {

    @Bean
    public DiscordClient getDiscordClient(@Value("${discord.token}") String token, List<EventListener> eventListeners) {
        DiscordClient client = new ClientBuilder(token).build();
        eventListeners.forEach(e -> client.getEventDispatcher().on(e.getEventType()).subscribe(e));
        client.login().subscribe();
        return client;
    }
}
