package com.discordbolt.boltbot.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import discord4j.core.DiscordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;

@RestController
@RequestMapping(path = "/status")
public class StatusController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DiscordClient client;

    @GetMapping(path = "")
    public ObjectNode getStatus() {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
        objectNode.with("discord").put("guilds", client.getGuilds().count().block());
        objectNode.with("discord").put("users", client.getUsers().count().block());
        objectNode.with("discord").with("shard").put("count", client.getConfig().getShardCount());
        objectNode.with("discord").with("shard").put("index", client.getConfig().getShardIndex());
        objectNode.with("discord").put("response_time", client.getResponseTime());
        return objectNode;
    }
}
