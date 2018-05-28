package com.discordbolt.boltbot.data.repositories;

import com.discordbolt.boltbot.data.objects.GuildData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildRepository extends CrudRepository<GuildData, Long> {

    List<GuildData> findByName(String name);
}
