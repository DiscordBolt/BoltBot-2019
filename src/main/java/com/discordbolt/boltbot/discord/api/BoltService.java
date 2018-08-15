package com.discordbolt.boltbot.discord.api;

import com.discordbolt.api.commands.BotCommand;
import com.discordbolt.api.commands.CommandContext;
import discord4j.core.DiscordClient;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Profile("prod")
@DependsOn("BeanUtil") //This ensures BeanUtil is setup before it is used by Bot Modules
public class BoltService {

    public static final String PACKAGE_PREFIX = "com.discordbolt.boltbot";
    private static final Logger LOGGER = LoggerFactory.getLogger(BoltService.class);

    private DiscordClient client;
    private List<BotModule> botModules;

    @Autowired
    public BoltService(DiscordClient client) {
        this.client = client;
        initModules();
    }

    private void initModules() {
        LOGGER.info("Registering Bolt Modules");

        botModules = new Reflections(PACKAGE_PREFIX).getSubTypesOf(BotModule.class).stream().map(c -> {
            try {
                BotModule m = c.getDeclaredConstructor().newInstance();
                m.initialize(client);
                return Optional.of(m);
            } catch (Exception e) {
                LOGGER.error("Unable to initialize module '" + c.getName() + "'", e);
                return Optional.<BotModule>empty();
            }
        }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public List<BotModule> getBotModules() {
        return Collections.unmodifiableList(botModules);
    }

    @BotCommand(command = "ping", description = "ping", usage = "ping", module = "misc")
    public static void ping(CommandContext context) {
        context.replyWith("Pong!").subscribe();
    }
}
