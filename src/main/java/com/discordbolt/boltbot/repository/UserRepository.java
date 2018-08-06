package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.entity.GuildData;
import com.discordbolt.boltbot.repository.entity.UserData;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserData, Long> {

    List<GuildData> findByName(String name);
}
