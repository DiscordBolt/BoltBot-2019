package net.ajpappas.discord.api.mysql;

import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.GuildUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.user.UserUpdateEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DataSync {

    public DataSync(IDiscordClient client) {
        client.getGuilds().forEach(g -> GuildJoinEvent(new GuildCreateEvent(g)));
    }

    @EventSubscriber
    public void GuildJoinEvent(GuildCreateEvent e) {
        // Process each user that should be added to the database
        e.getGuild().getUsers().forEach(u -> UserJoinEvent(new UserJoinEvent(e.getGuild(), u, LocalDateTime.now())));

        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("INSERT INTO guilds (guild_id, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name)");
            ps.setLong(1, e.getGuild().getLongID());
            ps.setString(2, e.getGuild().getName());
            ps.executeUpdate();
            Logger.trace("Created/updated database record of guild '" + e.getGuild().getName() + "' with ID " + e.getGuild().getStringID() + ".");
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while updating guild " + e.getGuild().getName());
            Logger.debug(ex);
        }
    }

    @EventSubscriber
    public void GuildLeaveEvent(GuildLeaveEvent e) {
        // Process each user that should be removed from the database
        e.getGuild().getUsers().forEach(u -> UserLeaveEvent(new UserLeaveEvent(e.getGuild(), u)));

        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("DELETE FROM guilds WHERE guild_id = ? LIMIT 1");
            ps.setLong(1, e.getGuild().getLongID());
            ps.executeUpdate();
            Logger.trace("Deleted database record of guild '" + e.getGuild().getName() + "' with ID " + e.getGuild().getStringID() + ".");
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while deleting guild " + e.getGuild().getName());
            Logger.debug(ex);
        }
    }

    @EventSubscriber
    public void GuildNameChangeEvent(GuildUpdateEvent e) {
        if (e.getOldGuild().getName().equals(e.getNewGuild().getName())) {
            return;
        }

        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("UPDATE guilds SET name = ? WHERE guild_id = ? LIMIT 1");
            ps.setString(1, e.getNewGuild().getName());
            ps.setLong(2, e.getNewGuild().getLongID());
            ps.executeUpdate();
            Logger.trace("Updated database record of guild '" + e.getNewGuild().getName() + "' with ID " + e.getNewGuild().getStringID() + ".");
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while updating guild " + e.getNewGuild().getName());
            Logger.debug(ex);
        }
    }

    @EventSubscriber
    public void UserJoinEvent(UserJoinEvent e) {
        if (e.getUser().isBot()) {
            return;
        }

        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("INSERT INTO users (user_id, username, discriminator, status, last_status_change) VALUES (?, ?, ?, ?, NOW()) ON DUPLICATE KEY UPDATE username=VALUES(username), discriminator=VALUES(discriminator), status=VALUES(status), last_status_change=(NOW())");
            ps.setLong(1, e.getUser().getLongID());
            ps.setString(2, e.getUser().getName());
            ps.setString(3, e.getUser().getDiscriminator());
            ps.setString(4, e.getUser().getPresence().getStatus().toString());
            ps.executeUpdate();
            Logger.trace("Created/updated database record of user '" + e.getUser().getName() + "' with ID " + e.getUser().getStringID() + ".");
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while updating user " + e.getUser().getName());
            Logger.debug(ex);
        }
    }

    @EventSubscriber
    public void UserLeaveEvent(UserLeaveEvent e) {
        if (e.getUser().isBot()) {
            return;
        }

        if (e.getClient().getGuilds().stream().filter(g -> !g.equals(e.getGuild())).anyMatch(g -> g.getUsers().contains(e.getUser()))) {
            return;
        }

        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("DELETE FROM users WHERE user_id = ? LIMIT 1");
            ps.setLong(1, e.getUser().getLongID());
            ps.executeUpdate();
            Logger.trace("Deleted database record of user '" + e.getUser().getName() + "' with ID " + e.getUser().getStringID() + ".");
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while deleting user " + e.getUser().getName());
            Logger.debug(ex);
        }
    }

    @EventSubscriber
    public void UsernameChangeEvent(UserUpdateEvent e) {
        if (e.getNewUser().isBot()) {
            return;
        }

        if (e.getOldUser().getName().equals(e.getNewUser().getName())) {
            return;
        }

        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("UPDATE users SET username = ? WHERE user_id = ? LIMIT 1");
            ps.setString(1, e.getNewUser().getName());
            ps.setLong(2, e.getNewUser().getLongID());
            ps.executeUpdate();
            Logger.trace("Updated database record of user '" + e.getNewUser().getName() + "' with ID " + e.getNewUser().getStringID() + ".");
        } catch (SQLException ex) {
            Logger.error("SQL Exception occurred while updating user " + e.getNewUser().getName());
            Logger.debug(ex);
        }
    }
}
