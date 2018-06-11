package com.discordbolt.boltbot.discord.api;

import discord4j.core.DiscordClient;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Profile("prod")
@DependsOn("BeanUtil") //This ensures BeanUtil is setup before it is used by Bot Modules
public class BoltService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoltService.class);

    private DiscordClient client;
    private List<BotModule> botModules;

    @Autowired
    public BoltService(DiscordClient client) {
        this.client = client;
        this.botModules = new ArrayList<>();
        initModules();
    }

    private void initModules() {
        LOGGER.info("Registering Bolt Modules");

        botModules = new Reflections("com.discordbolt.boltbot").getSubTypesOf(BotModule.class).stream().map(c -> {
            try {
                BotModule m = c.getDeclaredConstructor().newInstance();
                m.initialize(client);
                return m;
            } catch (Exception e) {
                LOGGER.error("Unable to initialize BotModule '" + c.getName() + "'", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<BotModule> getBotModules() {
        return Collections.unmodifiableList(botModules);
    }
}
