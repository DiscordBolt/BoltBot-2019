package Techtony96.Discord.modules.gametracker;

import Techtony96.Discord.modules.userstatus.UserStatusModule;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

/**
 * Created by Techt on 9/29/2016.
 */
public class GameTrackerModule implements IModule {

	public static IDiscordClient client;
	private String moduleName = "Game Tracker";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.5.0-SNAPSHOT";
	private String author = "Techtony96";
	protected static final String FILE_LOCATION = "/var/www/api/discord/user/";
	protected static final String FILE_TYPE = ".txt";

	@Override
	public void disable() {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is disabled.");
	}

	@Override
	public boolean enable(IDiscordClient client) {
		UserStatusModule.client = client;
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
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is READY.");
	}

	@EventSubscriber
	public void onUserGameUpdate(PresenceUpdateEvent e) {
		// TODO Called when a user starts/stops playing a game
	}

	/**
	 * Update information on all users
	 */
	private void updateAllUsers() {
		for (IGuild guild : client.getGuilds()) {
			for (IUser user : guild.getUsers()) {
				if (user.isBot())
					continue;
				// TODO Get all players playing a game (Used when a bot starts up)
			}
		}
	}
}
