package com.discordbolt.boltbot.web.controllers;

import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import com.discordbolt.boltbot.web.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/guilds")
public class GuildController {

    @Autowired
    private GuildRepository guildRepository;

    @GetMapping(path = "")
    public @ResponseBody
    Iterable<GuildData> getAllGuilds() {
        return guildRepository.findAll();
    }

    @GetMapping(path = "/{guild.id}")
    public @ResponseBody
    GuildData getGuildById(@PathVariable(name = "guild.id") long id) {
        return guildRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
