package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.entity.GuildData;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuildRepository extends MongoRepository<GuildData, Long> {

    List<GuildData> findByName(String name);
}
