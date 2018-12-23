package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<UserData> findById(Snowflake guildSnowflake) {
        return Optional.ofNullable(mongoTemplate.findById(guildSnowflake.asLong(), UserData.class));
    }
}
