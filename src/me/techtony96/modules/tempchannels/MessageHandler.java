package me.techtony96.modules.tempchannels;

import me.techtony96.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class MessageHandler {

	@EventSubscriber
	public void OnMesageEvent(MessageReceivedEvent e) {
		IMessage message = e.getMessage();
		IUser user = e.getMessage().getAuthor();
		IChannel channel = message.getChannel();
		String messageString = message.getContent();
		String[] args = messageString.split(" ");

		if (message.getContent().toLowerCase().startsWith("!create")) {
			// We need to handle the processing of creating a voice channel
			if (args.length <= 1) {
				try {
					channel.sendMessage(user.mention() + " Invalid syntax. !Create [Channel Name]");
				} catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
					Logger.error("Error while trying to send a message");
					ex.printStackTrace();
					return;
				}
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				sb.append(args[i] + " ");
			}

			ChannelManager.createChannel(user, sb.toString(), e.getMessage().getGuild());

		}
	}

}
