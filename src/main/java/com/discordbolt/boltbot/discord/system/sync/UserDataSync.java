package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.UserUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDataSync implements BotModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataSync.class);

    private UserRepository userRepository = BeanUtil.getBean(UserRepository.class);

    @Override
    public void initialize(DiscordClient client) {
        // Member join event
        client.getEventDispatcher().on(MemberJoinEvent.class)
                .map(MemberJoinEvent::getMember)
                .flatMap(member -> userRepository.findById(member.getId())
                        .defaultIfEmpty(new UserData(member))
                        .map(data -> data.update(member)))
                .flatMap(userRepository::save)
                .subscribe();


        // User update event
        client.getEventDispatcher().on(UserUpdateEvent.class)
                .map(UserUpdateEvent::getCurrent)
                .flatMap(user -> userRepository.findById(user.getId())
                        .defaultIfEmpty(new UserData(user))
                        .map(data -> data.update(user)))
                .flatMap(userRepository::save)
                .subscribe();


        // Join Guild
        client.getEventDispatcher().on(GuildCreateEvent.class)
                .map(GuildCreateEvent::getGuild)
                .flatMap(Guild::getMembers)
                .flatMap(member -> userRepository.findById(member.getId())
                        .defaultIfEmpty(new UserData(member))
                        .map(data -> data.update(member)))
                .flatMap(userRepository::save)
                .subscribe();
    }
}
