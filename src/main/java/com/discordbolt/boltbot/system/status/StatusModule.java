package com.discordbolt.boltbot.system.status;

import com.discordbolt.boltbot.system.CustomModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.modules.IModule;

import java.util.List;

/**
 * Created by Tony on 5/12/2017.
 */
public class StatusModule extends CustomModule implements IModule {

    public StatusModule(IDiscordClient client) {
        super(client, "Status Module", "1.0");
        updatePlayingText();
    }

    @EventSubscriber
    public void onGuildJoin(GuildCreateEvent e) {
        updatePlayingText();
    }

    @EventSubscriber
    public void onGuildLeave(GuildLeaveEvent e) {
        updatePlayingText();
    }

    @EventSubscriber
    public void onUserJoin(UserJoinEvent e) {
        updatePlayingText();
    }

    @EventSubscriber
    public void onUserLeave(UserLeaveEvent e) {
        updatePlayingText();
    }

    private void updatePlayingText() {
        int guildCount = client.getGuilds().size();
        int userCount = client.getGuilds().stream().map(IGuild::getUsers).mapToInt(List::size).sum();
        getClient().changePresence(StatusType.ONLINE, ActivityType.WATCHING, String.format("%d guild%s w/ %d users", guildCount, guildCount > 1 ? "s" : "", userCount));
    }
}
