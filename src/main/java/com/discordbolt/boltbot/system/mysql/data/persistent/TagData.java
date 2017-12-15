package com.discordbolt.boltbot.system.mysql.data.persistent;

import com.discordbolt.boltbot.system.mysql.MySQL;
import com.discordbolt.boltbot.system.mysql.data.Savable;
import com.discordbolt.boltbot.utils.Logger;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class TagData implements Savable {

    private static final int TAG_MAX_LENGTH = 50;
    private static final int CONTENT_MAX_LENGTH = 1000;

    private long tag_id;
    private long guild_id;
    private long user_id;
    private String name;
    private String content;

    private TagData(long tag_id, long guildId, long userId, String name, String content) {
        this.tag_id = tag_id;
        this.guild_id = guildId;
        this.user_id = userId;
        this.name = name.toLowerCase();
        this.content = content;
        tags.add(this);
    }

    public void setContent(String content) {
        if (content.length() > CONTENT_MAX_LENGTH)
            throw new IllegalArgumentException("Tag content can not be more than 1000 characters.");
        this.content = content;
        save();
    }

    @Override
    public boolean save() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("INSERT INTO tags (tag_id, guild_id, user_id, name, content) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE content=VALUES(content)");
            ps.setLong(1, getId());
            ps.setLong(2, getGuildId());
            ps.setLong(3, getUserId());
            ps.setBytes(4, getName().getBytes(StandardCharsets.UTF_16));
            ps.setBytes(5, getContent().getBytes(StandardCharsets.UTF_16));
            ps.executeUpdate();

            PreparedStatement ps2 = MySQL.getDataSource().getConnection().prepareStatement("SELECT tag_id FROM tags WHERE guild_id = ? AND name = ?");
            ps2.setLong(1, getGuildId());
            ps2.setBytes(2, getName().getBytes(StandardCharsets.UTF_16));
            ResultSet rs = ps2.executeQuery();
            if (rs.next()) {
                this.tag_id = rs.getLong("tag_id");
            } else {
                Logger.warning("Did not find TagData ID on lookup for '" + getName() + "' in guild '" + Long.toUnsignedString(getGuildId()) + "'.");
            }
            Logger.trace("Created/updated TagData: '" + getName() + "' with ID " + Long.toUnsignedString(getId()) + ".");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while creating/updating TagData: '" + Long.toUnsignedString(getId()) + "'.");
            Logger.debug(ex);
            return false;
        }
    }

    @Override
    public boolean delete() {
        if (getId() <= 0L) {
            Logger.error("Tried to delete TagData with invalid ID.");
            return false;
        }
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("DELETE FROM tags WHERE tag_id = ? LIMIT 1");
            ps.setLong(1, getId());
            ps.executeUpdate();
            tags.remove(this);
            Logger.trace("Deleted TagData: '" + getName() + "' with ID " + Long.toUnsignedString(getId()) + ".");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while deleting : '" + getName() + "' with ID '" + Long.toUnsignedString(getId()) + "'.");
            Logger.debug(ex);
            return false;
        }
    }

    @Override
    public long getId() {
        return getTagId();
    }

    public long getTagId() {
        return tag_id;
    }

    public long getGuildId() {
        return guild_id;
    }

    public long getUserId() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    /*
     * Static Methods
     */

    private static ArrayList<TagData> tags = new ArrayList<>();

    public static boolean fetch() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("SELECT * FROM tags;");
            ResultSet rs = ps.executeQuery();

            try {
                while (rs.next()) {
                    tags.add(new TagData(rs.getLong("tag_id"), rs.getLong("guild_id"), rs.getLong("user_id"), rs.getString("name"), rs.getString("content")));
                }
            } finally {
                ps.close();
            }

            Logger.trace("Fetched all TagData.");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while fetching TagData.");
            Logger.debug(ex);
            return false;
        }
    }

    public static TagData create(long guildId, long userId, String name, String content) {
        if (getById(guildId, name).isPresent())
            throw new IllegalStateException("Tag already exists!");
        if (name.length() > TAG_MAX_LENGTH)
            throw new IllegalArgumentException("Tag can not be more than 50 characters.");
        if (content.length() > CONTENT_MAX_LENGTH)
            throw new IllegalArgumentException("Tag content can not be more than 1000 characters.");

        TagData tagData = new TagData(0L, guildId, userId, name, content);
        tagData.save();
        return tagData;
    }

    public static Optional<TagData> getById(long guildId, String name) {
        return tags.stream().filter(t -> t.getGuildId() == guildId).filter(t -> t.getName().equals(name.toLowerCase())).findAny();
    }
}
