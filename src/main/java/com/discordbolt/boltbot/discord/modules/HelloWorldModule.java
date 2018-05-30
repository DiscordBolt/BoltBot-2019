package com.discordbolt.boltbot.discord.modules;

import com.discordbolt.boltbot.data.repositories.GuildRepository;
import com.discordbolt.boltbot.discord.EventListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldModule extends EventListener<MessageCreateEvent> {

    @Autowired
    private GuildRepository guildRepository;

    @Override
    public void accept(MessageCreateEvent event) {
        if (event.getMessage().getContent().isPresent() && event.getMessage().getContent().get().toLowerCase().equals("!hello")) {
            event.getMessage().getChannel().flatMap(channel -> channel.createMessage(spec -> spec.setContent("Hello World!"))).subscribe();
        }
    }
}
