package Techtony96.Discord.modules.userstatus;

import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.impl.obj.VoiceChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.modules.IModule;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserStatusModule implements IModule {

	public static IDiscordClient client;
	private String moduleName = "User Status Tracker";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.5.0-SNAPSHOT";
	private String author = "Techtony96";
	private final IVoiceChannel NOT_CONNECTED = new VoiceChannel(client, "$Not_Connected", "", null, "", 0, 0, 0);
	private final String FILE_LOCATION = "/var/www/api/discord/user/";
	private final String FILE_TYPE = ".txt";

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
		updateAllUsers();
	}

	@EventSubscriber
	public void onUserGameUpdate(PresenceUpdateEvent e) {
		updateUser(e.getUser());
	}

	/*
	 * Listeners
	 */
	@EventSubscriber
	public void onUserStatusChange(StatusChangeEvent e) {
		updateUser(e.getUser());
	}

	@EventSubscriber
	public void onVoiceChannelChange(UserVoiceChannelJoinEvent e) {
		updateUser(e.getUser());
	}

	@EventSubscriber
	public void onVoiceChannelChange(UserVoiceChannelLeaveEvent e) {
		updateUser(e.getUser());
	}

	@EventSubscriber
	public void onVoiceChannelChange(UserVoiceChannelMoveEvent e) {
		updateUser(e.getUser());
	}

	/**
	 * Update information on all users
	 */
	private void updateAllUsers() {
		for (IGuild guild : client.getGuilds()) {
			for (IUser user : guild.getUsers()) {
				updateUser(user);
			}
		}
	}

	/**
	 * Update the information stored on a user
	 *
	 * @param user The User to update
	 */
	public void updateUser(IUser user) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(FILE_LOCATION + user.getID() + FILE_TYPE, "UTF-8");
			writer.println("ID:" + user.getID());
			writer.println("Name:" + user.getName());
			writer.println("Discriminator:" + user.getDiscriminator());
			writer.println("Date:" + System.currentTimeMillis() + " #" + (new SimpleDateFormat("MM/dd/yy HH:mm:ss")).format(new Date(System.currentTimeMillis())));
			writer.println("OnlineStatus:" + user.getPresence().toString());
			writer.println("Game:" + user.getGame().orElse("$No_Game"));
			writer.println("VoiceChannels:" + user.getVoiceChannel().orElse(NOT_CONNECTED).getName());
		} catch (FileNotFoundException ex) {
			Logger.error("File was not found while updating a user.");
			Logger.debug(ex);
		} catch (UnsupportedEncodingException ex) {
			Logger.error("Encoding exception.");
			Logger.debug(ex);
		} finally {
			writer.close();
		}
	}
}
