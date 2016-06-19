package me.techtony96.modules.chat;

import me.techtony96.utils.Logger;
import sx.blah.discord.api.EventDispatcher;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.IModule;

public class ChatModule implements IModule {

	private String moduleName = "Chat Module";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.3.0";
	private String author = "Tony Pappas";
	public static IDiscordClient client;

	@Override
	public void disable() {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is disabled.");
	}

	@Override
	public boolean enable(IDiscordClient client) {
		this.client = client;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new MessageHandler());
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
