package com.discordbolt.boltbot.persistent.repositories;

import com.discordbolt.boltbot.persistent.objects.GuildData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildDataRepository extends CrudRepository<GuildData, Long> {

    List<GuildData> findByName(String name);
}
