package net.ajpappas.discord.modules.gametracker;

import net.ajpappas.discord.api.CustomModule;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.impl.events.user.UserUpdateEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.modules.IModule;

import java.util.HashMap;

/**
 * Created by Tony Pappas on 9/29/2016.
 */
public class GameTrackerModule extends CustomModule implements IModule {

    private static final int MIN_GAME_TIME = 15 * 60 * 1000; //15 Minutes
    private HashMap<Long, UserInfo> currentUsers = new HashMap<>();

    public GameTrackerModule() {
        super("GameTracker", "1.1");
    }

    // This is being handled by GuildCreateEvent now
    //@EventSubscriber
    //public void onReady(ReadyEvent e) {
    //    //Updates all user's usernames
    //    updateAllUsers();
    //}

    @EventSubscriber
    public void onGameChange(PresenceUpdateEvent e) {
        if (currentUsers.containsKey(e.getUser().getLongID())) { // User was already in a game
            UserInfo ui = currentUsers.get(e.getUser().getLongID());

            if (e.getNewPresence().getPlayingText().isPresent() && ui.getGame().equals(e.getNewPresence().getPlayingText().get())) { // Event was fired for same game they are already playing
                return;
            }
            // User is finished playing their game
            if (System.currentTimeMillis() - ui.getStartTime() >= MIN_GAME_TIME) {
                GameLog.addGameLog(e.getUser(), currentUsers.get(e.getUser().getLongID()).getGame(), ui.getStartTime(), System.currentTimeMillis());
            }
            currentUsers.remove(e.getUser().getLongID());
        }

        if (e.getNewPresence().getPlayingText().isPresent() && !e.getNewPresence().getStatus().equals(StatusType.STREAMING)) {
            currentUsers.put(e.getUser().getLongID(), new UserInfo(e.getUser(), e.getNewPresence().getPlayingText().get(), System.currentTimeMillis()));
        }
    }

    @EventSubscriber
    public void updateUsername(UserUpdateEvent e) {
        if (!e.getOldUser().getName().equals(e.getNewUser().getName()))
            GameLog.addUser(e.getNewUser());
    }

    @EventSubscriber
    public void joinGuild(GuildCreateEvent e) {
        GameLog.addUsers(e.getGuild().getUsers());

        for (IUser user : e.getGuild().getUsers()) {
            if (user.isBot())
                continue;
            if (user.getPresence().getPlayingText().isPresent() && !user.getPresence().getStatus().equals(StatusType.STREAMING))
                currentUsers.put(user.getLongID(), new UserInfo(user, user.getPresence().getPlayingText().get(), System.currentTimeMillis()));
        }

    }

    @EventSubscriber
    public void userJoin(UserJoinEvent e) {
        if (e.getUser().isBot())
            return;
        GameLog.addUser(e.getUser());

        if (e.getUser().getPresence().getPlayingText().isPresent() && !e.getUser().getPresence().getStatus().equals(StatusType.STREAMING))
            currentUsers.put(e.getUser().getLongID(), new UserInfo(e.getUser(), e.getUser().getPresence().getPlayingText().get(), System.currentTimeMillis()));
    }
}
