package com.discordbolt.boltbot.discord.system.status;

import com.discordbolt.boltbot.discord.EventListener;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusListener {

    @Autowired
    private StatusMessage manager;

    @Component
    private class ReadyListener extends EventListener<ReadyEvent> {

        @Override
        public void accept(ReadyEvent event) {
            manager.updateStatusMessage();
        }
    }

    @Component
    private class GuildJoinListener extends EventListener<GuildCreateEvent> {

        @Override
        public void accept(GuildCreateEvent event) {
            manager.updateStatusMessage();
        }
    }

    @Component
    private class GuildLeaveListener extends EventListener<GuildDeleteEvent> {

        @Override
        public void accept(GuildDeleteEvent event) {
            manager.updateStatusMessage();
        }
    }

    @Component
    private class MemberJoinListener extends EventListener<MemberJoinEvent> {

        @Override
        public void accept(MemberJoinEvent event) {
            manager.updateStatusMessage();
        }
    }

    @Component
    private class MemberLeaveListener extends EventListener<MemberLeaveEvent> {

        @Override
        public void accept(MemberLeaveEvent event) {
            manager.updateStatusMessage();
        }
    }
}
