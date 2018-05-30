package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.EventListener;
import com.discordbolt.boltbot.repository.UserRepository;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLeaveListener extends EventListener<MemberLeaveEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLeaveListener.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void accept(MemberLeaveEvent event) {
        userRepository.findById(event.getUser().getId().asLong()).ifPresentOrElse(userData -> userRepository.delete(userData), () -> LOGGER.error("Unable to find User '{}' while attempting to delete UserData.", event.getUser().getId().asString()));
    }
}
