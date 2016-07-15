package main.java.Techtony96.Discord.modules.privatemessageresponder;

import main.java.Techtony96.Discord.utils.ChannelUtil;
import main.java.Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.PrivateChannel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.modules.IModule;

public class PrivateMessageResponder implements IModule {

	public static IDiscordClient client;
	private String moduleName = "Private Message Responder";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.5.0";
	private String author = "Techtony96";

	@Override
	public void disable() {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is disabled.");
	}

	@Override
	public boolean enable(IDiscordClient client) {
		PrivateMessageResponder.client = client;
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
	public void OnMesageEvent(MessageReceivedEvent e) {
		IChannel channel = e.getMessage().getChannel();

		if (channel instanceof PrivateChannel) {
			ChannelUtil.sendMessage(channel, "Please do not PM me!");
		}
	}

	@EventSubscriber
	public void onReady(ReadyEvent e) {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is READY.");
	}
}
