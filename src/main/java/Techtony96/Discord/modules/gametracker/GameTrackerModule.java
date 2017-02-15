package Techtony96.Discord.modules.gametracker;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.modules.IModule;

import java.sql.Date;
import java.util.HashMap;

/**
 * Created by Tony Pappas on 9/29/2016.
 */
public class GameTrackerModule extends CustomModule implements IModule {

	private HashMap<IUser, Long> currentUsers = new HashMap<>();


	public GameTrackerModule() {
		super("GameTracker", "1.1");
	}

	@EventSubscriber
	public void onReady(ReadyEvent e) {
		//Updates all user's usernames
		updateAllUsers();
	}

	@EventSubscriber
	public void onUserGameUpdate(StatusChangeEvent e) {
		if (e.getNewStatus().getType() == Status.StatusType.NONE) {
			if (currentUsers.get(e.getUser()) == null) {
				// User is not stored in the list, we somehow missed him so we will ignore that he just stopped playing a game.
				Logger.warning(e.getUser().getName() + " was not in currentUsers to store his game data.");
				return;
			}
			GameLog.addGameLog(e.getUser(), e.getOldStatus().getStatusMessage(), new Date(currentUsers.get(e.getUser())), new Date(System.currentTimeMillis()));
		} else if (e.getNewStatus().getType() == Status.StatusType.GAME) {
			currentUsers.put(e.getUser(), System.currentTimeMillis());
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
			    GameLog.addUser(user);
				if (user.getStatus().getType() == Status.StatusType.NONE || user.getStatus().getType() == Status.StatusType.STREAM)
					continue;
				currentUsers.put(user, System.currentTimeMillis());
			}
		}
	}
}
