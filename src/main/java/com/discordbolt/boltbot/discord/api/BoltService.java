package com.discordbolt.boltbot.discord.api;

import com.discordbolt.api.commands.BotCommand;
import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CommandManager;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.GuildRepository;
import discord4j.core.DiscordClient;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@DependsOn("BeanUtil") //This ensures BeanUtil is setup before it is used by Bot Modules
public class BoltService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoltService.class);
    private static final String PACKAGE_PREFIX = "com.discordbolt.boltbot";

    private DiscordClient client;
    private List<BotModule> botModules;

    @Autowired
    public BoltService(DiscordClient client) {
        this.client = client;
        initModules();
        initCommands();
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

    private void initCommands() {
        // Initialize command manager (scans and registers commands with @BotCommand)
        CommandManager commandManager = new CommandManager(client, PACKAGE_PREFIX);
        // Restore saved per-guild command prefixes
        StreamSupport.stream(BeanUtil.getBean(GuildRepository.class)
                .findAll()
                .spliterator(), false)
                .filter(data -> data.getCommandPrefix() != null)
                .forEach(data -> commandManager.setCommandPrefix(data.getId(), data.getCommandPrefix()));

        commandManager.onCommandExecution(context -> {
            // TODO store stats about each command execution.
            // Neo4J https://projects.spring.io/spring-data-neo4j/
            //
            // https://medium.com/@joeclever/using-multiple-datasources-with-spring-boot-and-spring-data-6430b00c02e7
            // http://www.baeldung.com/spring-data-jpa-multiple-databases
        });
    }

    public List<BotModule> getBotModules() {
        return Collections.unmodifiableList(botModules);
    }

    @BotCommand(command = "ping", description = "ping", usage = "ping", module = "misc")
    public static void ping(CommandContext context) {
        context.replyWith("Pong!").subscribe();
    }
}
