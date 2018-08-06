package com.discordbolt.boltbot.web.controllers;

import com.discordbolt.boltbot.repository.GuildRepository;
import com.discordbolt.boltbot.repository.entity.GuildData;
import com.discordbolt.boltbot.web.exceptions.EntityNotFoundException;
import com.discordbolt.boltbot.web.models.GuildModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/guilds")
public class GuildController {

    @Autowired
    private GuildRepository guildRepository;

    @GetMapping(path = "")
    public ResponseEntity<GuildModel> getAllGuilds() {
        return new ResponseEntity<>(new GuildModel(guildRepository.findAll()), HttpStatus.OK);
    }

    @GetMapping(path = "/{guild.id}")
    public ResponseEntity<GuildData> getGuildById(@PathVariable(name = "guild.id") long id) {
        return new ResponseEntity<>(guildRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Guild not found with ID " + id)), HttpStatus.OK);
    }
}
