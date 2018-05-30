package com.discordbolt.boltbot.discord;

import discord4j.core.event.domain.Event;
import org.springframework.core.GenericTypeResolver;

import java.util.function.Consumer;

public abstract class EventListener<T extends Event> implements Consumer<T> {

    Class<T> getEventType() {
        return (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), EventListener.class);
    }
}
