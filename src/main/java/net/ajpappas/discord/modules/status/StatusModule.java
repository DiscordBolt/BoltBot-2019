package net.ajpappas.discord.modules.status;

import net.ajpappas.discord.api.CustomModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 5/12/2017.
 */
public class StatusModule extends CustomModule implements IModule {

    private int guildCount = 0;
    private int userCount = 0;

    public StatusModule(IDiscordClient client) {
        super(client, "Status Module", "1.0");
    }

    @EventSubscriber
    public void onGuildJoin(GuildCreateEvent e) {
        guildCount++;
        userCount += e.getGuild().getUsers().size();
        updatePlayingText();
    }

    @EventSubscriber
    public void onGuildLeave(GuildLeaveEvent e) {
        guildCount--;
        userCount -= e.getGuild().getUsers().size();
        updatePlayingText();
    }

    @EventSubscriber
    public void onUserJoin(UserJoinEvent e) {
        userCount++;
        updatePlayingText();
    }

    @EventSubscriber
    public void onUserLeave(UserLeaveEvent e) {
        userCount--;
        updatePlayingText();
    }

    private void updatePlayingText() {
        getClient().changePlayingText(String.format("%d guild%s with %d users", guildCount, guildCount > 1 ? "s" : "", userCount));
    }
}
