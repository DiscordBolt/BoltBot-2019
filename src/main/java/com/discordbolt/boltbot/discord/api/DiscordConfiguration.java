package com.discordbolt.boltbot.discord.api;

import discord4j.core.ClientBuilder;
import discord4j.core.DiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class DiscordConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordConfiguration.class);

    @Bean
    public DiscordClient getDiscordClient(@Value("${discord.token}") String token, @Value("${boltbot.version}") String version) {
        LOGGER.info("Starting BoltBot version {}", version);
        LOGGER.info("Starting configuration of Discord Client");
        DiscordClient client = new ClientBuilder(token).build();
        LOGGER.info("Logging into Discord...");
        client.login().subscribe();
        return client;
    }
}
