package com.discordbolt.boltbot.discord.modules.seen;

import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.UserRepository;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.presence.Status;
import reactor.util.function.Tuples;

import java.time.Instant;

public class SeenListener {

    private DiscordClient client;
    private UserRepository userRepository;

    public SeenListener(DiscordClient client) {
        this.client = client;
        userRepository = BeanUtil.getBean(UserRepository.class);

        //Update all users join guild
        client.getEventDispatcher().on(GuildCreateEvent.class)
                .map(GuildCreateEvent::getGuild)
                .flatMap(Guild::getMembers)
                .map(m -> Tuples.of(m, m.getPresence(), userRepository.findById(m.getId())))
                .flatMap(tuple -> tuple.getT2().map(t -> Tuples.of(tuple.getT1(), t, tuple.getT3())))
                .flatMap(tuple -> tuple.getT3().map(t -> Tuples.of(tuple.getT1(), tuple.getT2(), t)))
                .map(tuple -> {
                    // Save the current status of each user
                    tuple.getT3().setLastStatusChange(Instant.now());
                    tuple.getT3().setLastStatus(tuple.getT2().getStatus());
                    // Save when they were last online if not online
                    if (tuple.getT2().getStatus() != Status.ONLINE)
                        tuple.getT3().setLastOnline(Instant.now());
                    return tuple.getT3();
                })
                .flatMap(userRepository::save)
                .subscribe();

        // Keep track of when a user was last online
        client.getEventDispatcher().on(PresenceUpdateEvent.class)
                .filter(event -> event.getOld().isPresent())
                .filter(event -> event.getOld().get().getStatus() == Status.ONLINE)
                .filter(event -> event.getCurrent().getStatus() != Status.ONLINE)
                .flatMap(PresenceUpdateEvent::getMember)
                .map(Member::getId)
                .flatMap(userRepository::findById)
                .doOnNext(userData -> userData.setLastOnline(Instant.now()))
                .flatMap(userRepository::save)
                .subscribe();

        // Keep track of the most recent presence
        client.getEventDispatcher().on(PresenceUpdateEvent.class)
                .flatMap(PresenceUpdateEvent::getMember)
                .map(m -> Tuples.of(m, m.getPresence(), userRepository.findById(m.getId())))
                .flatMap(tuple -> tuple.getT2().map(t -> Tuples.of(tuple.getT1(), t, tuple.getT3())))
                .flatMap(tuple -> tuple.getT3().map(t -> Tuples.of(tuple.getT1(), tuple.getT2(), t)))
                .map(tuple -> {
                    tuple.getT3().setLastStatus(tuple.getT2().getStatus());
                    tuple.getT3().setLastStatusChange(Instant.now());
                    return tuple.getT3();
                })
                .flatMap(userRepository::save)
                .subscribe();
    }
}
