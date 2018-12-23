package com.discordbolt.boltbot.repository.custom;

import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.object.util.Snowflake;

import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<UserData> findById(Snowflake userSnowflake);
}
