package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.data.repositories.GuildRepository;
import com.discordbolt.boltbot.discord.EventListener;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GuildLeaveListener extends EventListener<GuildDeleteEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildLeaveListener.class);

    @Autowired
    private GuildRepository guildRepository;

    @Override
    public void accept(GuildDeleteEvent guildDeleteEvent) {
        guildRepository.findById(guildDeleteEvent.getGuildId().asLong()).ifPresentOrElse(guildData -> guildRepository.delete(guildData), () -> LOGGER.error("Unable to find Guild '{}' while attempting to delete GuildData.", guildDeleteEvent.getGuildId().asString()));
    }
}
