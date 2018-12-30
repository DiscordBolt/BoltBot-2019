package com.discordbolt.boltbot.web.controllers;

import com.discordbolt.boltbot.discord.api.BoltService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.management.OperatingSystemMXBean;
import discord4j.core.DiscordClient;
import discord4j.core.util.VersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@RestController
@RequestMapping(path = "/status")
public class StatusController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DiscordClient client;

    @Autowired
    private BoltService boltService;

    @GetMapping(path = "")
    public ObjectNode getStatus() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Runtime runtime = Runtime.getRuntime();

        // Memory in bytes
        long totalSystem = osBean.getTotalPhysicalMemorySize();
        long usedSystem = totalSystem - osBean.getFreePhysicalMemorySize();
        long totalJvm = runtime.totalMemory();
        long usedJvm = totalJvm - runtime.freeMemory();

        ObjectNode status = mapper.createObjectNode();
        status.with("boltbot").put("version", boltService.getVersion());
        status.with("boltbot").put("d4j_version", VersionUtil.getProperties().getProperty(VersionUtil.APPLICATION_VERSION));
        status.with("boltbot").put("java_version", Runtime.version().toString());
        status.with("boltbot").put("uptime", runtimeBean.getUptime());
        status.with("boltbot").put("cpu_load", osBean.getProcessCpuLoad());
        status.with("boltbot").put("ram_total", totalJvm);
        status.with("boltbot").put("ram_used", usedJvm);

        status.with("system").put("cpu_total", runtime.availableProcessors());
        status.with("system").put("cpu_load", osBean.getSystemCpuLoad());
        status.with("system").put("ram_total", totalSystem);
        status.with("system").put("ram_used", usedSystem);

        status.with("discord").put("guilds", client.getGuilds().count().block());
        status.with("discord").put("users", client.getUsers().count().block());
        status.with("discord").with("shard").put("count", client.getConfig().getShardCount());
        status.with("discord").with("shard").put("index", client.getConfig().getShardIndex());
        status.with("discord").put("response_time", client.getResponseTime());
        return status;
    }
}
