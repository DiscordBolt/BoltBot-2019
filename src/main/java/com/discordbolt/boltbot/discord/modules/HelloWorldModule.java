package com.discordbolt.boltbot.discord.modules;

import com.discordbolt.boltbot.discord.api.BotModule;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class HelloWorldModule implements BotModule {

    private DiscordClient client;

    @Override
    public void initialize(DiscordClient client) {
        this.client = client;
        registerEvents();
    }

    private void registerEvents() {
        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(this::accept);
        // Can also do the filtering here
        /*
        client.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(m -> m.getContent().isPresent())
            .filter(m -> m.getContent().get().toLowerCase().equals("!hello"))
            .subscribe(this::accept);
         */
    }

    private void accept(MessageCreateEvent event) {
        if (event.getMessage().getContent().isPresent() && event.getMessage().getContent().get().equalsIgnoreCase("!hello")) {
            event.getMessage().getChannel().flatMap(channel -> channel.createMessage(spec -> spec.setContent("Hello World!"))).subscribe();
        }
    }
}
