package Techtony96.Discord.modules.gametracker;

import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.modules.IModule;

import java.util.HashMap;

/**
 * Created by Tony Pappas on 9/29/2016.
 */
public class GameTrackerModule implements IModule {

	public static IDiscordClient client;
	private String moduleName = "Game Tracker";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.5.0-SNAPSHOT";
	private String author = "Techtony96";

	protected static final String FILE_TYPE = ".txt";
	protected static final String FILE_LOCATION = "/var/www/html/api/discord/gametracker/users";
	protected static final String GAME_INDEX_LOCATION = "/var/www/html/api/discord/gametracker/games" + FILE_TYPE;

	private HashMap<IUser, Long> currentUsers = new HashMap<>();

	@Override
	public void disable() {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is disabled.");
	}

	@Override
	public boolean enable(IDiscordClient client) {
		GameTrackerModule.client = client;
		return true;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getMinimumDiscord4JVersion() {
		return moduleMinimumVersion;
	}

	@Override
	public String getName() {
		return moduleName;
	}

	@Override
	public String getVersion() {
		return moduleVersion;
	}

	@EventSubscriber
	public void onReady(ReadyEvent e) {
		updateAllUsers();
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is READY.");
	}

	@EventSubscriber
	public void onUserGameUpdate(StatusChangeEvent e) {
		if (e.getNewStatus().getType() == Status.StatusType.NONE) {
			if (currentUsers.get(e.getUser()) == null) {
				// User is not stored in the list, we somehow missed him so we will ignore that he just stopped playing a game.
				Logger.warning(e.getUser().getName() + " was not in currentUsers to store his game data.");
				return;
			}
			FileIO.addGameTime(e.getUser(), e.getOldStatus().getStatusMessage(), currentUsers.get(e.getUser()), System.currentTimeMillis());
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
				if (user.isBot() || user.getStatus().getType() == Status.StatusType.NONE || user.getStatus().getType() == Status.StatusType.STREAM)
					continue;
				currentUsers.put(user, System.currentTimeMillis());
			}
		}
	}
}
