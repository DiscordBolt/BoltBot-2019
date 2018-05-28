package com.discordbolt.boltbot.web;

import com.discordbolt.boltbot.data.objects.GuildData;
import com.discordbolt.boltbot.data.repositories.GuildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping(path = "/guilds")
public class GuildController {

    @Autowired
    private GuildRepository guildRepository;

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<GuildData> getAllGuilds() {
        return guildRepository.findAll();
    }

    @GetMapping(path = "/search")
    public @ResponseBody
    Optional<GuildData> getGuildById(@RequestParam long id) {
        return guildRepository.findById(id);
    }
}
