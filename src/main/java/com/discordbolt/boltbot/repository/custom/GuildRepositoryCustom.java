package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.object.util.Snowflake;

import java.util.Optional;

public interface GuildRepositoryCustom {

    Optional<GuildData> findById(Snowflake guildSnowflake);
}
