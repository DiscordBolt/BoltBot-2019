package com.discordbolt.boltbot.api.mysql.data.persistent;

import com.discordbolt.boltbot.api.mysql.MySQL;
import com.discordbolt.boltbot.api.mysql.data.Savable;
import com.discordbolt.boltbot.utils.Logger;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

public class UserData implements Savable {

    private long user_id;
    private String username;
    private String discriminator;
    private StatusType status;
    private Instant last_status_change;

    private UserData(long userId, String username, String discriminator, String status, long lastStatusChange) {
        this.user_id = userId;
        this.username = username;
        this.discriminator = discriminator;
        this.status = status != null ? StatusType.get(status) : null;
        this.last_status_change = lastStatusChange > 0L ? Instant.ofEpochMilli(lastStatusChange) : null;
        users.put(getId(), this);
    }

    public void setUsername(String username, String discriminator) {
        this.username = username;
        this.discriminator = discriminator;
        save();
    }

    public void setStatus(StatusType status) {
        this.status = status;
        this.last_status_change = Instant.now();
        save();
    }

    @Override
    public boolean save() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("INSERT INTO users (user_id, username, discriminator, status, last_status_change) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username=VALUES(username), discriminator=VALUES(discriminator), status=VALUES(status), last_status_change=VALUES(last_status_change);");
            ps.setLong(1, getUserId());
            ps.setBytes(2, getUsername().getBytes(StandardCharsets.UTF_16));
            ps.setString(3, getDiscriminator());
            ps.setString(4, getStatus() != null ? getStatus().toString() : null);
            ps.setTimestamp(5, getLastStatusChange() != null ? Timestamp.from(getLastStatusChange()) : null);
            ps.executeUpdate();
            Logger.trace("Created/updated UserData: '" + getUsername() + "' with ID " + Long.toUnsignedString(getUserId()) + ".");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while creating/updating UserData: '" + getUsername() + "'.");
            Logger.debug(ex);
            return false;
        }
    }

    @Override
    public boolean delete() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("DELETE FROM users WHERE user_id = ? LIMIT 1");
            ps.setLong(1, getUserId());
            ps.executeUpdate();
            users.remove(getUserId());
            Logger.trace("Deleted UserData: '" + getUsername() + "' with ID " + Long.toUnsignedString(getUserId()) + ".");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while deleting UserData: '" + getUsername() + "'.");
            Logger.debug(ex);
            return false;
        }
    }

    @Override
    public long getId() {
        return getUserId();
    }

    public long getUserId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public StatusType getStatus() {
        return status;
    }

    public Instant getLastStatusChange() {
        return last_status_change;
    }

    /*
     * Static Methods
     */

    private static HashMap<Long, UserData> users = new HashMap<>();

    public static boolean fetch() {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("SELECT * FROM users;");
            ResultSet rs = ps.executeQuery();

            try {
                while (rs.next()) {
                    long longTime = rs.getTimestamp("last_status_change") != null ? rs.getTimestamp("last_status_change").getTime() : 0L;
                    new UserData(rs.getLong("user_id"), rs.getString("username"), rs.getString("discriminator"), rs.getString("status"), longTime);
                }
            } finally {
                ps.close();
            }

            Logger.trace("Fetched all UserData.");
            return true;
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while fetching UserData.");
            Logger.debug(ex);
            return false;
        }
    }

    public static UserData getOrCreate(IUser user) {
        Optional<UserData> oud = getById(user.getLongID());
        if (oud.isPresent()) {
            UserData ud = oud.get();
            if (!user.getName().equals(ud.getUsername())) {
                ud.setUsername(user.getName(), user.getDiscriminator());
            }
            return ud;
        }

        UserData userData = new UserData(user.getLongID(), user.getName(), user.getDiscriminator(), null, 0L);
        userData.save();
        return userData;
    }

    public static Optional<UserData> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public static Optional<UserData> getByUsername(String username, boolean caseSensitive) {
        return users.values().stream().filter(u -> caseSensitive ? u.getUsername().equals(username) : u.getUsername().equalsIgnoreCase(username)).findAny();
    }

    public static Optional<UserData> getByUsername(String username) {
        return getByUsername(username, true);
    }
}
