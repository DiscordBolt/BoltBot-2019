package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.custom.UserRepositoryCustom;
import com.discordbolt.boltbot.repository.entity.UserData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveMongoRepository<UserData, Long>, UserRepositoryCustom {

    Flux<UserData> findByName(String name);
}
