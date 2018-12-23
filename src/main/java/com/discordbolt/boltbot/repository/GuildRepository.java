package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.custom.GuildRepositoryCustom;
import com.discordbolt.boltbot.repository.entity.GuildData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GuildRepository extends MongoRepository<GuildData, Long>, GuildRepositoryCustom {

    List<GuildData> findByName(String name);
}
