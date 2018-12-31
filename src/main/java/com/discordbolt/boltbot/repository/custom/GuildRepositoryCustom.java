package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

public interface GuildRepositoryCustom {

    Mono<GuildData> findById(Snowflake guildSnowflake);
}
