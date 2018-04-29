package com.discordbolt.boltbot.persistent.objects;

import com.discordbolt.boltbot.persistent.Hibernate;
import com.discordbolt.boltbot.persistent.PersistentObject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;

@Entity
@Table(name = "guilds")
public class GuildData extends PersistentObject {

    private static Logger LOGGER = LoggerFactory.getLogger(GuildData.class);
    private static HashMap<Long, GuildData> cache = new HashMap<>();


    @Id
    private long guildId;

    @Column(nullable = false)
    private String name;

    private String commandPrefix;

    private String tagPrefix;

    private long streamAnnounceChannel;


    @Override
    public void save() {
        LOGGER.trace("Saving GuildData '{}'", getId());
        Session session = Hibernate.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(this);
        session.getTransaction().commit();
        session.close();

        cache.put(this.getId(), this);
    }

    @Override
    public void delete() {
        LOGGER.trace("Deleting GuildData '{}'", getId());
        final Session session = Hibernate.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(this);
        session.getTransaction().commit();
        session.close();
        cache.remove(this.getId());
    }

    @Override
    public long getId() {
        return getGuildId();
    }

    public long getGuildId() {
        return guildId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        save();
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
        save();
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
        save();
    }

    public long getStreamAnnounceChannel() {
        return streamAnnounceChannel;
    }

    public void setStreamAnnounceChannel(long streamAnnounceChannel) {
        this.streamAnnounceChannel = streamAnnounceChannel;
        save();
    }
}
