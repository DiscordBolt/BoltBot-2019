package com.discordbolt.boltbot.discord.system.sync;

import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.UserUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDataSync implements BotModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataSync.class);

    private UserRepository userRepository = BeanUtil.getBean(UserRepository.class);

    @Override
    public void initialize(DiscordClient client) {
        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(this::memberJoin);
        client.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(this::memberLeave);
        client.getEventDispatcher().on(UserUpdateEvent.class).subscribe(this::memberUpdate);
        client.getEventDispatcher().on(GuildCreateEvent.class).subscribe(this::updateGuildMembers);
    }

    private void memberJoin(MemberJoinEvent event) {
        userRepository.findById(event.getMember().getId().asLong()).ifPresentOrElse(userData -> userRepository.save(userData.update(event.getMember())), () -> userRepository.save(new UserData(event.getMember())));
    }

    private void memberLeave(MemberLeaveEvent event) {
        userRepository.findById(event.getUser().getId().asLong()).ifPresentOrElse(userData -> userRepository.delete(userData), () -> LOGGER.error("Unable to find User '{}' while attempting to delete UserData.", event.getUser().getId().asString()));
    }

    private void memberUpdate(UserUpdateEvent event) {
        userRepository.findById(event.getCurrent().getId().asLong()).ifPresentOrElse(userData -> userRepository.save(userData.update(event.getCurrent())), () -> userRepository.save(new UserData(event.getCurrent())));
    }

    private void updateGuildMembers(GuildCreateEvent event) {
        //TODO Is it possible to make this a batch update? (Currently it updates/creates users individually
        event.getGuild().getMembers().subscribe(member -> userRepository.findById(member.getId().asLong()).ifPresentOrElse(userData -> userRepository.save(userData.update(member)), () -> userRepository.save(new UserData(member))));
    }
}
