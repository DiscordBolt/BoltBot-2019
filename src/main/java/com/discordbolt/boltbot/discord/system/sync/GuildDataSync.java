package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.GuildUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildDataSync implements BotModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildDataSync.class);

    private GuildRepository guildRepository = BeanUtil.getBean(GuildRepository.class);

    @Override
    public void initialize(DiscordClient client) {
        client.getEventDispatcher().on(GuildCreateEvent.class).subscribe(this::guildJoin);
        client.getEventDispatcher().on(GuildDeleteEvent.class).subscribe(this::guildLeave);
        client.getEventDispatcher().on(GuildUpdateEvent.class).subscribe(this::guildUpdate);
    }

    private void guildJoin(GuildCreateEvent event) {
        guildRepository.findById(event.getGuild().getId().asLong()).ifPresentOrElse(guildData -> guildRepository.save(guildData.update(event.getGuild())), () -> guildRepository.save(new GuildData(event.getGuild())));
    }

    private void guildLeave(GuildDeleteEvent guildDeleteEvent) {
        guildRepository.findById(guildDeleteEvent.getGuildId().asLong()).ifPresentOrElse(guildData -> guildRepository.delete(guildData), () -> LOGGER.error("Unable to find Guild '{}' while attempting to delete GuildData.", guildDeleteEvent.getGuildId().asString()));
    }

    private void guildUpdate(GuildUpdateEvent event) {
        guildRepository.findById(event.getCurrent().getId().asLong()).ifPresentOrElse(guildData -> guildRepository.save(guildData.update(event.getCurrent())), () -> guildRepository.save(new GuildData(event.getCurrent())));
    }
}
