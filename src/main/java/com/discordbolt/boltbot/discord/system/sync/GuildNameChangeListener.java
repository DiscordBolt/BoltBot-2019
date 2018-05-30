package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.EventListener;
import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.event.domain.guild.GuildUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GuildNameChangeListener extends EventListener<GuildUpdateEvent> {

    @Autowired
    private GuildRepository guildRepository;

    @Override
    public void accept(GuildUpdateEvent event) {
        guildRepository.findById(event.getCurrent().getId().asLong()).ifPresentOrElse(guildData -> guildRepository.save(guildData.update(event.getCurrent())), () -> guildRepository.save(new GuildData(event.getCurrent())));
    }
}
