package com.discordbolt.boltbot.system.mysql.data.persistent;

import com.discordbolt.boltbot.system.mysql.MySQL;
import com.discordbolt.boltbot.system.mysql.data.Savable;
import com.discordbolt.boltbot.utils.Logger;
import sx.blah.discord.handle.obj.IGuild;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

public class GuildData implements Savable {

    private long guild_id;
    private String name;
    private String command_prefix;
    private String tag_prefix;

    private GuildData(long guildId, String name, String commandPrefix, String tagPrefix) {
        this.guild_id = guildId;
        this.name = name;
        this.command_prefix = commandPrefix;
        this.tag_prefix = tagPrefix;
        guilds.put(getId(), this);
    }

    public void setName(String name) {
        this.name = name;
        save();
    }

    public void setCommandPrefix(String commandPrefix) {
        this.command_prefix = commandPrefix;
        save();
    }

    public void setTagPrefix(String tagPrefix) {
        this.tag_prefix = tagPrefix;
        save();
    }

    @Override
    public boolean save() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("INSERT INTO guilds (guild_id, name, command_prefix, tag_prefix) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), command_prefix=VALUES(command_prefix), tag_prefix=VALUES(tag_prefix);");
            ps.setLong(1, getGuildId());
            ps.setBytes(2, getName().getBytes(StandardCharsets.UTF_16));
            ps.setString(3, getCommandPrefix());
            ps.setString(4, getTagPrefix());
            ps.executeUpdate();
            Logger.trace("Created/updated GuildData: '" + getName() + "' with ID " + Long.toUnsignedString(getGuildId()) + ".");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while creating/updating GuildData: '" + getName() + "'.");
            Logger.debug(ex);
            return false;
        }
    }

    @Override
    public boolean delete() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("DELETE FROM guilds WHERE guild_id = ? LIMIT 1");
            ps.setLong(1, getGuildId());
            ps.executeUpdate();
            guilds.remove(getGuildId());
            Logger.trace("Deleted GuildData: '" + getName() + "' with ID " + Long.toUnsignedString(getGuildId()) + ".");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while deleting GuildData: '" + getName() + "'.");
            Logger.debug(ex);
            return false;
        }
    }

    @Override
    public long getId() {
        return getGuildId();
    }

    public long getGuildId() {
        return guild_id;
    }

    public String getName() {
        return name;
    }

    public String getCommandPrefix() {
        return command_prefix;
    }

    public String getTagPrefix() {
        return tag_prefix;
    }

    /*
     * Static Methods
     */

    private static HashMap<Long, GuildData> guilds = new HashMap<>();

    public static boolean fetch() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("SELECT * FROM guilds;");
            ResultSet rs = ps.executeQuery();

            try {
                while (rs.next()) {
                    GuildData gd = new GuildData(rs.getLong("guild_id"), rs.getString("name"), rs.getString("command_prefix"), rs.getString("tag_prefix"));
                    guilds.put(gd.getId(), gd);
                }
            } finally {
                ps.close();
            }

            Logger.trace("Fetched all GuildData.");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while fetching GuildData.");
            Logger.debug(ex);
            return false;
        }
    }

    public static GuildData getOrCreate(IGuild guild) {
        Optional<GuildData> ogd = getById(guild.getLongID());
        if (ogd.isPresent()) {
            GuildData gd = ogd.get();
            if (!guild.getName().equals(gd.getName())) {
                gd.setName(guild.getName());
            }
            return gd;
        }

        GuildData guildData = new GuildData(guild.getLongID(), guild.getName(), null, null);
        guildData.save();
        return guildData;
    }

    public static Optional<GuildData> getById(long guildId) {
        return Optional.ofNullable(guilds.get(guildId));
    }

    public static Optional<GuildData> getByName(String name) {
        return guilds.values().stream().filter(g -> g.getName().equals(name)).findAny();
    }
}
