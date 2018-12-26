package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildDataSync implements BotModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildDataSync.class);

    private GuildRepository guildRepository = BeanUtil.getBean(GuildRepository.class);

    @Override
    public void initialize(DiscordClient client) {
        // Guild join event
        client.getEventDispatcher().on(GuildCreateEvent.class)
                .map(GuildCreateEvent::getGuild)
                .flatMap(guild -> guildRepository.findById(guild.getId())
                        .defaultIfEmpty(new GuildData(guild))
                        .map(data -> data.update(guild)))
                .flatMap(guildRepository::save)
                .subscribe();

        // Guild update event
        client.getEventDispatcher().on(GuildUpdateEvent.class)
                .map(GuildUpdateEvent::getCurrent)
                .flatMap(guild -> guildRepository.findById(guild.getId())
                        .defaultIfEmpty(new GuildData(guild))
                        .map(data -> data.update(guild)))
                .flatMap(guildRepository::save)
                .subscribe();
    }
}
