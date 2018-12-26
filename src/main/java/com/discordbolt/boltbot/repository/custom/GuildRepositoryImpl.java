package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

public class GuildRepositoryImpl implements GuildRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public GuildRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<GuildData> findById(Snowflake guildSnowflake) {
        return mongoTemplate.findById(guildSnowflake.asLong(), GuildData.class);
    }
}
