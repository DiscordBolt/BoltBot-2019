package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.EventListener;
import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GuildJoinListener extends EventListener<GuildCreateEvent> {

    @Autowired
    private GuildRepository guildRepository;

    @Override
    public void accept(GuildCreateEvent event) {
        guildRepository.findById(event.getGuild().getId().asLong()).ifPresentOrElse(guildData -> guildRepository.save(guildData.update(event.getGuild())), () -> guildRepository.save(new GuildData(event.getGuild())));
    }
}
