package Techtony96.Discord.modules.gametracker;

import Techtony96.Discord.api.CustomModule;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.modules.IModule;

import java.util.HashMap;

/**
 * Created by Tony Pappas on 9/29/2016.
 */
public class GameTrackerModule extends CustomModule implements IModule {

    private HashMap<String, UserInfo> currentUsers = new HashMap<>();

    public GameTrackerModule() {
        super("GameTracker", "1.1");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        //Updates all user's usernames
        updateAllUsers();
    }

    @EventSubscriber
    public void onGameChange(PresenceUpdateEvent e) {
        if (currentUsers.containsKey(e.getUser().getID())) { // User was already in a game
            UserInfo ui = currentUsers.get(e.getUser().getID());

            if (e.getNewPresence().getPlayingText().isPresent() && ui.getGame().equals(e.getNewPresence().getPlayingText().get())) { // Event was fired for same game they are already playing
                return;
            }
            // User is finished playing their game
            GameLog.addGameLog(e.getUser(), currentUsers.get(e.getUser().getID()).getGame(), ui.getStartTime(), System.currentTimeMillis());
            currentUsers.remove(e.getUser().getID());
        }

        if (e.getNewPresence().getPlayingText().isPresent() && !e.getNewPresence().getStatus().equals(StatusType.STREAMING)) {
            currentUsers.put(e.getUser().getID(), new UserInfo(e.getUser(), e.getNewPresence().getPlayingText().get(), System.currentTimeMillis()));
        }
    }

    /**
     * Update information on all users
     */
    private void updateAllUsers() {
        for (IGuild guild : client.getGuilds()) {
            for (IUser user : guild.getUsers()) {
                if (user.isBot())
                    continue;
                try {
                    GameLog.addUser(user);
                } catch (IllegalStateException e) {

                }
                if (user.getPresence().getPlayingText().isPresent() && !user.getPresence().getStatus().equals(StatusType.STREAMING))
                    currentUsers.put(user.getID(), new UserInfo(user, user.getPresence().getPlayingText().get(), System.currentTimeMillis()));
            }
        }
    }
}
