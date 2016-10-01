package Techtony96.Discord.modules.tempchannels;

import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.IModule;

public class TempChannelModule implements IModule {

	public static IDiscordClient client;
	private String moduleName = "Temporary Voice Channels";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.5.0-SNAPSHOT";
	private String author = "Techtony96";

	@Override
	public void disable() {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is disabled.");
	}

	@Override
	public boolean enable(IDiscordClient client) {
		TempChannelModule.client = client;
		client.getDispatcher().registerListener(new MessageHandler());
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
}
