package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.entity.GuildData;
import com.discordbolt.boltbot.repository.entity.UserData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserData, Long> {

    List<GuildData> findByName(String name);
}
