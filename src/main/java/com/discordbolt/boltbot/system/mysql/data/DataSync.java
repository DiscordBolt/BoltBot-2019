package com.discordbolt.boltbot.system.mysql.data;

import com.discordbolt.boltbot.system.mysql.data.persistent.GuildData;
import com.discordbolt.boltbot.system.mysql.data.persistent.TagData;
import com.discordbolt.boltbot.system.mysql.data.persistent.UserData;
import com.discordbolt.boltbot.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.GuildUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.user.UserUpdateEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DataSync {

    public DataSync(IDiscordClient client) {
        Logger.info("Fetching database, this may take some time.");
        UserData.fetch();
        GuildData.fetch();
        TagData.fetch();
        Logger.info("Updating database, this may take some time.");
        client.getGuilds().forEach(GuildData::getOrCreate);
        client.getGuilds().stream().flatMap(g -> g.getUsers().stream()).forEach(UserData::getOrCreate);
        Logger.info("Done with database!");
    }

    @EventSubscriber
    public void GuildJoinEvent(GuildCreateEvent e) {
        // Create the guild
        GuildData.getOrCreate(e.getGuild());

        // Update and create all users of this guild (ignoring bots)
        e.getGuild().getUsers().stream().filter(u -> !u.isBot()).forEach(UserData::getOrCreate);
    }

    @EventSubscriber
    public void GuildLeaveEvent(GuildLeaveEvent e) {
        // Delete the guild
        GuildData.getById(e.getGuild().getLongID()).ifPresent(GuildData::delete);

        // Delete users if they are not in any of the other guilds
        Set<IUser> currentUsers = e.getClient().getGuilds().stream()    // Get a stream of all guilds
                .filter(g -> !g.equals(e.getGuild()))                   // Confirm that the guild left is removed from the stream
                .flatMap(g -> g.getUsers().stream())                    // Convert the stream to IUser
                .collect(Collectors.toSet());                           // Collect the stream to a set of IUsers
        e.getGuild().getUsers().stream()                                // Get a stream of users in guild left
                .filter(u -> !currentUsers.contains(u))                 // Filter out users that are contained in other guilds
                .map(IUser::getLongID)                                  // Convert to a stream of Long user IDs
                .map(UserData::getById)                                 // Convert to a stream of Optional<UserData>
                .filter(Optional::isPresent)                            // Filter to only UserData that is present
                .map(Optional::get)                                     // Convert to a stream of UserData
                .forEach(UserData::delete);                             // Delete UserData
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
