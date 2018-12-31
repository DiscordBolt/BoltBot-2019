package com.discordbolt.boltbot.web.controllers;

import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/guilds")
public class GuildController {

    @Autowired
    private GuildRepository guildRepository;

    @GetMapping(path = "")
    public Flux<GuildData> getAllGuilds() {
        return guildRepository.findAll().sort();
    }

    @GetMapping(path = "/{guild.id}")
    public Mono<ResponseEntity<GuildData>> getGuildById(@PathVariable(name = "guild.id") long id) {
        return guildRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
