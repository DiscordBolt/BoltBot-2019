package com.discordbolt.boltbot;

import com.discordbolt.boltbot.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class BoltBot {

    public static final Path BOLTBOT_PROPERTIES = Paths.get("BoltBot.properties"),
            DATABASE_PROPERTIES = Paths.get("Database.properties"),
            TWITCH_PROPERTIES = Paths.get("Twitch.properties");

    private static final Logger LOGGER = LoggerFactory.getLogger(BoltBot.class);
    private static IDiscordClient client;

    public static void main(String[] args) {
        LOGGER.info("Starting BoltBot v{}", getVersion());

        try {
            PropertiesUtil.loadPropertiesFile(BOLTBOT_PROPERTIES, DATABASE_PROPERTIES, TWITCH_PROPERTIES);
        } catch (IOException e) {
            LOGGER.error("Unable to load properties file.", e);
            System.exit(1);
        }

        Optional<String> token = PropertiesUtil.getValue(BOLTBOT_PROPERTIES, "TOKEN");
        if (token.isPresent()) {
            client = new ClientBuilder().withToken(token.get()).build();
        } else {
            LOGGER.error("Token not present in BoltBot.properties");
            System.exit(1);
        }

        client.getDispatcher().registerListener((IListener<ReadyEvent>) (ReadyEvent e) -> {
            LOGGER.info("Client is READY!");
            registerModules();
        });
    }


    public static String getVersion() {
        return PropertiesUtil.getValue(BOLTBOT_PROPERTIES, "VERSION").orElse("DEVELOPMENT");
    }

    public static IDiscordClient getClient() {
        return client;
    }

    private static void registerModules() {

    }
}
