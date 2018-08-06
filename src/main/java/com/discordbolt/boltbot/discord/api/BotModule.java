package com.discordbolt.boltbot.discord.api;

import discord4j.core.DiscordClient;

public interface BotModule {

    void initialize(DiscordClient client);
}
