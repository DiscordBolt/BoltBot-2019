package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.custom.GuildRepositoryCustom;
import com.discordbolt.boltbot.repository.entity.GuildData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface GuildRepository extends ReactiveMongoRepository<GuildData, Long>, GuildRepositoryCustom {

    Flux<GuildData> findByName(String name);
}
