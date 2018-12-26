package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

public interface UserRepositoryCustom {

    Mono<UserData> findById(Snowflake userSnowflake);
}
