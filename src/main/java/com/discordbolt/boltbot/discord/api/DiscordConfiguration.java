package com.discordbolt.boltbot.discord.api;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration("DiscordConfiguration")
@Profile("prod")
public class DiscordConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordConfiguration.class);

    private DiscordClient client;

    public DiscordConfiguration(@Value("${discord.token}") String token) {
        LOGGER.info("Starting configuration of Discord Client");
        client = new DiscordClientBuilder(token).build();
    }

    @Bean
    public DiscordClient getClient() {
        return client;
    }

    protected void login() {
        LOGGER.info("Logging into Discord...");
        client.login().subscribe(); // In most cases .block() should be used to keep the thread alive. (Spring keeps non-daemon threads running)
    }
}
