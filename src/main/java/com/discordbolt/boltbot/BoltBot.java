package com.discordbolt.boltbot;

import com.discordbolt.boltbot.persistent.repositories.GuildDataRepository;
import com.discordbolt.boltbot.util.PropertiesUtil;
import discord4j.core.ClientBuilder;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class BoltBot {

    public static final Path BOLTBOT_PROPERTIES = Paths.get("BoltBot.properties"),
            DATABASE_PROPERTIES = Paths.get("Database.properties"),
            TWITCH_PROPERTIES = Paths.get("Twitch.properties");

    private static final Logger LOGGER = LoggerFactory.getLogger(BoltBot.class);

    private static DiscordClient client;

    public static void main(String[] args) {
        LOGGER.info("Starting BoltBot");

        try {
            PropertiesUtil.loadPropertiesFile(BOLTBOT_PROPERTIES, DATABASE_PROPERTIES, TWITCH_PROPERTIES);
        } catch (IOException e) {
            LOGGER.error("Unable to load properties files.", e);
            System.exit(1);
        }

        LOGGER.info("Version: {}", getVersion());
        LOGGER.info("Logging into {} guilds.", );

        Optional<String> token = PropertiesUtil.getValue(BOLTBOT_PROPERTIES, "TOKEN");
        if (token.isPresent()) {
            client = new ClientBuilder(token.get()).build();
            //TODO Switch this to registering in client builder (When D4J adds it)
            getClient().getEventDispatcher().on(ReadyEvent.class).subscribe(BoltBot::registerModules);
            getClient().login().block();
        } else {
            LOGGER.error("Token not present in BoltBot.properties");
            System.exit(1);
        }

        LOGGER.warn("Disconnected from Discord. No further reconnect attempts will be made.");
    }


    public static String getVersion() {
        return PropertiesUtil.getValue(BOLTBOT_PROPERTIES, "VERSION").orElse("DEVELOPMENT");
    }

    public static DiscordClient getClient() {
        return client;
    }

    private static void registerModules(ReadyEvent event) {

    }
}
