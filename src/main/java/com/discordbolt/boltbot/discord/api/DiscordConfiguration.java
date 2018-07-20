package com.discordbolt.boltbot.discord.api;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
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
        DiscordClient client = new DiscordClientBuilder(token).build();
        LOGGER.info("Logging into Discord...");
        client.login().subscribe(); // In most cases .block() should be used to keep the thread alive. (Spring keeps non-daemon threads running)
        return client;
    }
}
