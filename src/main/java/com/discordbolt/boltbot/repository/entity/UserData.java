package com.discordbolt.boltbot.repository.entity;

import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
public class UserData implements Comparable<UserData> {

    @Id
    private long id;
    private String name;
    private String discriminator;
    private Status lastStatus;
    private Instant lastOnline;
    private Instant lastStatusChange;

    protected UserData() {
    }

    public UserData(long id, String name, String discriminator) {
        this.id = id;
        this.name = name;
        this.discriminator = discriminator;
    }

    public UserData(User user) {
        this.id = user.getId().asLong();
        this.name = user.getUsername();
        this.discriminator = user.getDiscriminator();
    }

    public UserData update(User user) {
        setName(user.getUsername());
        setDiscriminator(user.getDiscriminator());
        return this;
    }

    @Override
    public String toString() {
        return String.format("UserData[id=%d, name='%s', discriminator='%d']", getId(), getName(), getDiscriminator());
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof UserData && ((UserData) other).getId() == this.getId());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserData setName(String name) {
        this.name = name;
        return this;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public UserData setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
        return this;
    }

    public Instant getLastOnline() {
        return lastOnline;
    }

    public UserData setLastOnline(Instant instant) {
        this.lastOnline = instant;
        return this;
    }

    public Instant getLastStatusChange() {
        return lastStatusChange;
    }

    public UserData setLastStatusChange(Instant instant) {
        this.lastStatusChange = instant;
        return this;
    }

    public Status getLastStatus() {
        return lastStatus;
    }

    public UserData setLastStatus(Status status) {
        this.lastStatus = status == null ? Status.OFFLINE : status;
        return this;
    }

    @Override
    public int compareTo(@NotNull UserData other) {
        return Long.compare(this.getId(), other.getId());
    }
}
