package com.discordbolt.boltbot.discord.modules.seen;

import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;

import java.time.Instant;

public class SeenListener {

    private DiscordClient client;
    private UserRepository userRepository;

    public SeenListener(DiscordClient client) {
        this.client = client;
        userRepository = BeanUtil.getBean(UserRepository.class);

        //Update all users join guild
        //FIXME make this better
        client.getEventDispatcher().on(GuildCreateEvent.class)
                .map(GuildCreateEvent::getGuild)
                .flatMap(Guild::getMembers)
                .subscribe(member -> {
                    UserData data = userRepository.findById(member.getId()).orElse(null);
                    if (data != null) {
                        data.setLastStatusChange(Instant.now());
                        Status currentPresence = member.getPresence().map(Presence::getStatus).block();
                        data.setLastStatus(currentPresence);
                        if (currentPresence == Status.ONLINE) {
                            data.setLastOnline(Instant.now());
                        }
                        userRepository.save(data);
                    }

                });

        // Keep track of when a user was last online
        client.getEventDispatcher().on(PresenceUpdateEvent.class)
                .filter(e -> e.getCurrent().getStatus() != Status.ONLINE)
                .subscribe(e -> userRepository.findById(e.getUserId()).ifPresent(userData -> userRepository.save(userData.setLastOnline(Instant.now()))));

        // Keep track of the most recent presence
        client.getEventDispatcher().on(PresenceUpdateEvent.class)
                .subscribe(e -> userRepository.findById(e.getUserId()).ifPresent(userData -> {
                            userData.setLastStatusChange(Instant.now());
                            userData.setLastStatus(e.getCurrent().getStatus());
                            userRepository.save(userData);
                        }
                ));
    }
}
