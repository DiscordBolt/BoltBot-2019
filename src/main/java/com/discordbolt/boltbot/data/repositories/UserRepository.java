package com.discordbolt.boltbot.data.repositories;

import com.discordbolt.boltbot.data.objects.GuildData;
import com.discordbolt.boltbot.data.objects.UserData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserData, Long> {

    List<GuildData> findByName(String name);
}
