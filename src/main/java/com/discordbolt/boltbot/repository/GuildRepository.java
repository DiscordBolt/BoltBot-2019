package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.entity.GuildData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildRepository extends CrudRepository<GuildData, Long> {

    List<GuildData> findByName(String name);
}
