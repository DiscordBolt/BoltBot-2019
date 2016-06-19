package me.techtony96;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import me.techtony96.utils.Logger;

public class Instance {

	private volatile IDiscordClient client;
	private String token;
	private final AtomicBoolean reconnect = new AtomicBoolean(true);

	public Instance(String token) {
		this.token = token;
	}

	public void login() throws DiscordException {
		client = new ClientBuilder().withToken(token).login();
		client.getDispatcher().registerListener(this);
	}

	@EventSubscriber
	public void onReady(ReadyEvent event) {
		Logger.info("Discord.java is READY!");
	}

	@EventSubscriber
	public void onDisconnect(DiscordDisconnectedEvent event) {
		CompletableFuture.runAsync(() -> {
			if (reconnect.get()) {
				Logger.info("Reconnecting bot");
				try {
					login();
				} catch (DiscordException e) {
					Logger.warning("Failed to reconnect bot");
					Logger.debug(e);
				}
			}
		});
	}

	public void terminate() {
		reconnect.set(false);
		try {
			client.logout();
		} catch (HTTP429Exception | DiscordException e) {
			Logger.warning("Logout failed");
			Logger.debug(e);
		}
	}
}