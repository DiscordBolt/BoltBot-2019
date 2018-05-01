package com.discordbolt.boltbot.persistent.repositories;

import com.discordbolt.boltbot.persistent.objects.UserData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDataRepository extends CrudRepository<UserData, Long> {

    List<UserData> findByName(String name);
}
