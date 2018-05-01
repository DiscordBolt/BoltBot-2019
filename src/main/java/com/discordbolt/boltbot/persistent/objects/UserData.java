package com.discordbolt.boltbot.persistent.objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserData {

    @Id
    private long id;
    private String name;
    private int discriminator;

    protected UserData() {
    }

    public UserData(long id, String name, int discriminator) {
        this.id = id;
        this.name = name;
        this.discriminator = discriminator;
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

    public void setName(String name) {
        this.name = name;
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(int discriminator) {
        this.discriminator = discriminator;
    }
}
