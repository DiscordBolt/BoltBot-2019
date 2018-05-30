package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.EventListener;
import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.event.domain.UserUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNameChangeListener extends EventListener<UserUpdateEvent> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void accept(UserUpdateEvent event) {
        userRepository.findById(event.getCurrent().getId().asLong()).ifPresentOrElse(userData -> userRepository.save(userData.update(event.getCurrent())), () -> userRepository.save(new UserData(event.getCurrent())));
    }
}
