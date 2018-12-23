package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.custom.UserRepositoryCustom;
import com.discordbolt.boltbot.repository.entity.UserData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<UserData, Long>, UserRepositoryCustom {

    List<UserData> findByName(String name);
}
