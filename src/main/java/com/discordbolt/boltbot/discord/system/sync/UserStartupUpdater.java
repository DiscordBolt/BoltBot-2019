package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.data.objects.UserData;
import com.discordbolt.boltbot.data.repositories.UserRepository;
import com.discordbolt.boltbot.discord.EventListener;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserStartupUpdater extends EventListener<ReadyEvent> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void accept(ReadyEvent event) {
        //TODO Is it possible to make this a batch update? (Currently it updates/creates users individually
        event.getClient().getGuilds().flatMap(Guild::getMembers).distinct(Member::getId).subscribe(member -> userRepository.findById(member.getId().asLong()).ifPresentOrElse(userData -> userRepository.save(userData.update(member)), () -> userRepository.save(new UserData(member))));
    }
}
