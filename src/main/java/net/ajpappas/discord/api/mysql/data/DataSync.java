package net.ajpappas.discord.api.mysql.data;

import net.ajpappas.discord.api.mysql.data.persistent.GuildData;
import net.ajpappas.discord.api.mysql.data.persistent.TagData;
import net.ajpappas.discord.api.mysql.data.persistent.UserData;
import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.GuildUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.user.UserUpdateEvent;

import java.time.LocalDateTime;

public class DataSync {

    public DataSync(IDiscordClient client) {
        Logger.info("Fetching/updating database, this may take some time.");
        UserData.fetch();
        GuildData.fetch();
        TagData.fetch();
        client.getGuilds().forEach(g -> GuildJoinEvent(new GuildCreateEvent(g)));
        Logger.info("Done with database!");
    }

    @EventSubscriber
    public void GuildJoinEvent(GuildCreateEvent e) {
        // Process each user that should be added to the database
        e.getGuild().getUsers().forEach(u -> UserJoinEvent(new UserJoinEvent(e.getGuild(), u, LocalDateTime.now())));

        GuildData.getOrCreate(e.getGuild());
    }

    @EventSubscriber
    public void GuildLeaveEvent(GuildLeaveEvent e) {
        // Process each user that should be removed from the database
        e.getGuild().getUsers().forEach(u -> UserLeaveEvent(new UserLeaveEvent(e.getGuild(), u)));

        GuildData.getById(e.getGuild().getLongID()).ifPresent(GuildData::delete);
    }

    @EventSubscriber
    public void GuildNameChangeEvent(GuildUpdateEvent e) {
        if (e.getOldGuild().getName().equals(e.getNewGuild().getName())) {
            return;
        }

        GuildData.getById(e.getNewGuild().getLongID()).ifPresent(gd -> gd.setName(e.getNewGuild().getName()));
    }

    @EventSubscriber
    public void UserJoinEvent(UserJoinEvent e) {
        if (e.getUser().isBot()) {
            return;
        }

        UserData.getOrCreate(e.getUser());
    }

    @EventSubscriber
    public void UserLeaveEvent(UserLeaveEvent e) {
        if (e.getUser().isBot()) {
            return;
        }

        // Check if the user that left is in any other guilds the bot is in
        if (e.getClient().getGuilds().stream().filter(g -> !g.equals(e.getGuild())).anyMatch(g -> g.getUsers().contains(e.getUser()))) {
            return;
        }

        UserData.getById(e.getUser().getLongID()).ifPresent(UserData::delete);
    }

    @EventSubscriber
    public void UsernameChangeEvent(UserUpdateEvent e) {
        if (e.getNewUser().isBot()) {
            return;
        }

        if (e.getOldUser().getName().equals(e.getNewUser().getName())) {
            return;
        }

        UserData.getById(e.getNewUser().getLongID()).ifPresent(ud -> ud.setUsername(e.getNewUser().getName(), e.getNewUser().getDiscriminator()));
    }
}
