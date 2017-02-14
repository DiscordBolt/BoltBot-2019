package Techtony96.Discord.modules.tempchannels;

import Techtony96.Discord.modules.tempchannels.exceptions.DuplicateChannelException;
import Techtony96.Discord.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class MessageHandler {

	private IDiscordClient client;

	public MessageHandler(IDiscordClient client){
		this.client = client;
	}

	@EventSubscriber
	public void OnMesageEvent(MessageReceivedEvent e) {
		IMessage message = e.getMessage();
		IUser user = e.getMessage().getAuthor();
		IChannel channel = message.getChannel();
		String messageString = message.getContent();
		String[] args = messageString.split(" ");
		String command = args[0].toLowerCase();

		if (command.equals("!create")) {
			// We need to handle the processing of creating a voice channel
			if (args.length <= 1) {
				ChannelUtil.sendMessage(channel, user.mention() + " Invalid syntax. !Create -Private [Channel Name]");
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
				ChannelManager.createChannel(client, user, sb.toString(), e.getMessage().getGuild(), privateChannel);
			} catch (DuplicateChannelException ex) {
				ChannelUtil.sendMessage(channel, user.mention() + ", you already own a temporary channel, delete it with !Delete");
			}
		} else if (command.equals("!add")) {
			TemporaryChannel ch = ChannelManager.getChannel(user);

			if (ch == null) {
				ChannelUtil.sendMessage(channel, user.mention() + ", you do not currently have a temporary voice channel. Create one with !Create");
				return;
			}

			if (!ch.isPrivate()) {
				ChannelUtil.sendMessage(channel, user.mention() + ", your temporary voice channel isn't private.");
				return;
			}

			if (message.getMentions().size() < 1) {
				ChannelUtil.sendMessage(channel, user.mention() + ", no users were @Mentioned in your message.");
				return;
			}

			for (IUser mentioned : message.getMentions()) {
				ch.giveUserPermission(mentioned);
			}
		} else if (command.equals("!delete")) {
			if (ChannelManager.getChannel(user) == null) {
				ChannelUtil.sendMessage(channel, user.mention() + ", you do not have a temporary voice channel.");
				return;
			}

			ChannelManager.removeChannel(ChannelManager.getChannel(user));
			ChannelUtil.sendMessage(channel, user.mention() + ", successfully deleted your temporary channel.");
			return;
		}
	}
}
