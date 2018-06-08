package com.discordbolt.boltbot.discord;

import discord4j.core.ClientBuilder;
import discord4j.core.DiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DiscordConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordConfiguration.class);

    @Bean
    public DiscordClient getDiscordClient(@Value("${discord.token}") String token, @Value("${boltbot.version}") String version, List<EventListener> eventListeners) {
        LOGGER.info("Starting BoltBot version {}", version);
        LOGGER.info("Starting configuration of Discord Client");
        DiscordClient client = new ClientBuilder(token).build();
        // Register all existing EventListeners
        LOGGER.info("Registering listeners");
        eventListeners.forEach(e -> client.getEventDispatcher().on(e.getEventType()).subscribe(e));
        LOGGER.info("Logging into Discord...");
        client.login().subscribe();
        return client;
    }
}
