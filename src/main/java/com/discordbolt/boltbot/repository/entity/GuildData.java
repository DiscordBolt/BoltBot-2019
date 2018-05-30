package com.discordbolt.boltbot.repository.entity;

import discord4j.core.object.entity.Guild;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guilds")
public class GuildData {

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

    public void setName(String name) {
        this.name = name;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public long getStreamAnnounceChannel() {
        return streamAnnounceChannel;
    }

    public void setStreamAnnounceChannel(long streamAnnounceChannel) {
        this.streamAnnounceChannel = streamAnnounceChannel;
    }
}
