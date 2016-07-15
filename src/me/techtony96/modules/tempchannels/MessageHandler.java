package me.techtony96.modules.tempchannels;

import me.techtony96.modules.tempchannels.exceptions.DuplicateChannelException;
import me.techtony96.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.PrivateChannel;
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

		if (channel instanceof PrivateChannel) {
			try {
				channel.sendMessage("Please do not PM me.");
				return;
			} catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
				Logger.error("Error while trying to send a message");
				ex.printStackTrace();
				return;
			}
		}

		if (message.getContent().toLowerCase().startsWith("!create")) {
			// We need to handle the processing of creating a voice channel
			if (args.length <= 1) {
				try {
					channel.sendMessage(user.mention() + " Invalid syntax. !Create -Private [Channel Name]");
				} catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
					Logger.error("Error while trying to send a message");
					ex.printStackTrace();
					return;
				}
				return;
			}
			boolean privateChannel = false;
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-private") || args[i].equalsIgnoreCase("-p")) {
					privateChannel = true;
					continue;
				}
				sb.append(args[i] + " ");
			}
			
			sb.deleteCharAt(sb.length() - 1);

			try {
				ChannelManager.createChannel(user, sb.toString(), e.getMessage().getGuild(), privateChannel);
			} catch (DuplicateChannelException ex) {
				try {
					channel.sendMessage(user.mention() + ", you already own a temporary channel, delete it with !Delete");
				} catch (RateLimitException | MissingPermissionsException | DiscordException ex2) {
					Logger.error("Error while trying to send a message");
					ex2.printStackTrace();
					return;
				}
			}

		} else if (message.getContent().toLowerCase().startsWith("!add")) {
			if (message.getMentions().size() < 1) {
				try {
					channel.sendMessage(user.mention() + ", no users were @Mentioned in your message.");
				} catch (RateLimitException | MissingPermissionsException | DiscordException ex) {
					Logger.error("Error while trying to send a message");
					ex.printStackTrace();
					return;
				}
				return;
			}

			TemporaryChannel ch = ChannelManager.getChannel(user);

			if (ch == null) {
				try {
					channel.sendMessage(user.mention() + ", you do not currently have a temporary voice channel. Create one with !Create");
					return;
				} catch (RateLimitException | MissingPermissionsException | DiscordException ex) {
					Logger.error("Error while trying to send a message");
					ex.printStackTrace();
					return;
				}
			}

			if (!ch.isPrivate()) {
				try {
					channel.sendMessage(user.mention() + ", your temporary voice channel isn't private.");
					return;
				} catch (RateLimitException | MissingPermissionsException | DiscordException ex) {
					Logger.error("Error while trying to send a message");
					ex.printStackTrace();
					return;
				}
			}

			for (IUser mentioned : message.getMentions()) {
				ch.giveUserPermission(mentioned);
			}
		} else if (message.getContent().toLowerCase().startsWith("!delete")) {
			if (ChannelManager.getChannel(user) == null) {
				try {
					channel.sendMessage(user.mention() + ", you do not have a temporary voice channel.");
					return;
				} catch (RateLimitException | MissingPermissionsException | DiscordException ex) {
					Logger.error("Error while trying to send a message");
					ex.printStackTrace();
					return;
				}
			}
			ChannelManager.removeChannel(ChannelManager.getChannel(user));
			try {
				channel.sendMessage(user.mention() + ", successfully deleted your temporary channel.");
			} catch (RateLimitException | MissingPermissionsException | DiscordException ex) {
				Logger.error("Error while trying to send a message");
				ex.printStackTrace();
				return;
			}
			return;
		}
	}

}
