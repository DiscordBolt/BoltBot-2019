package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<UserData> findById(Snowflake guildSnowflake) {
        return mongoTemplate.findById(guildSnowflake.asLong(), UserData.class);
    }
}
