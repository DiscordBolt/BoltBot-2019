package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

public class GuildRepositoryImpl implements GuildRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public GuildRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<GuildData> findById(Snowflake guildSnowflake) {
        return Optional.ofNullable(mongoTemplate.findById(guildSnowflake.asLong(), GuildData.class));
    }
}
