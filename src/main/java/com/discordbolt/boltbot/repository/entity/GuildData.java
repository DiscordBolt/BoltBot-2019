package com.discordbolt.boltbot.repository.entity;

import discord4j.core.object.entity.Guild;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "guilds")
public class GuildData implements Comparable<GuildData> {

    @Id
    private long id;

    private String name;
    private String commandPrefix;
    private String tagPrefix;
    private long streamAnnounceChannel;

    protected GuildData() {
    }

    public GuildData(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public GuildData(Guild guild) {
        this.id = guild.getId().asLong();
        this.name = guild.getName();
    }

    public GuildData update(Guild guild) {
        this.setName(guild.getName());
        return this;
    }

    @Override
    public String toString() {
        return String.format("GuildData[id=%d, name='%s']", getId(), getName());
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof GuildData && ((GuildData) other).getId() == this.getId());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GuildData setName(String name) {
        this.name = name;
        return this;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public GuildData setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
        return this;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public GuildData setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
        return this;
    }

    public long getStreamAnnounceChannel() {
        return streamAnnounceChannel;
    }

    public GuildData setStreamAnnounceChannel(long streamAnnounceChannel) {
        this.streamAnnounceChannel = streamAnnounceChannel;
        return this;
    }

    @Override
    public int compareTo(@NotNull GuildData other) {
        return Long.compare(this.getId(), other.getId());
    }
}
