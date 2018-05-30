package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.EventListener;
import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserJoinListener extends EventListener<MemberJoinEvent> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void accept(MemberJoinEvent event) {
        userRepository.findById(event.getMember().getId().asLong()).ifPresentOrElse(userData -> userRepository.save(userData.update(event.getMember())), () -> userRepository.save(new UserData(event.getMember())));
    }
}
