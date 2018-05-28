package com.discordbolt.boltbot.discord.system.status;

import discord4j.core.DiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StatusMessage {

    @Autowired
    private DiscordClient client;

    void updateStatusMessage() {
        Mono<Long> guildCount = client.getGuilds().count();
        Mono<Integer> userCount = client.getGuilds().map(guild -> guild.getMemberCount().orElse(0)).reduce(0, (a, b) -> a + b);

        guildCount.zipWith(userCount).flatMap(t -> setStatusMessage(t.getT1(), t.getT2())).subscribe();
    }

    private Mono<Void> setStatusMessage(Long guildCount, Integer memberCount) {
        return client.updatePresence(Presence.online(Activity.playing(String.format("%d guild%s w/ %d users", guildCount, guildCount > 1 ? "s" : "", memberCount))));
    }
}
