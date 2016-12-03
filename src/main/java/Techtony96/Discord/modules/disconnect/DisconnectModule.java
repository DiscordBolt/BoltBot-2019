package Techtony96.Discord.modules.disconnect;

import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.ExceptionMessage;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.UUID;

/**
 * Created by Tony Pappas on 12/2/2016.
 */
public class DisconnectModule implements IModule {

	public static IDiscordClient client;
	private String moduleName = "Disconnect";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.5.0";
	private String author = "Techtony96";

	@Override
	public void disable() {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is disabled.");
	}

	@Override
	public boolean enable(IDiscordClient client) {
		DisconnectModule.client = client;
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
		IMessage message = e.getMessage();
		IUser user = e.getMessage().getAuthor();
		IChannel channel = message.getChannel();
		String messageString = message.getContent();
		String[] args = messageString.split(" ");
		String command = args[0].toLowerCase();

		if (command.equals("!disconnect")) {
			for (IRole role : user.getRolesForGuild(message.getGuild())) {
				if (role.getPermissions().contains(Permissions.VOICE_MOVE_MEMBERS)) {
					break;
				}
				ChannelUtil.sendMessage(channel, user.mention() + " " + ExceptionMessage.PERMISSION_DENIED);
				return;
			}

			if (message.getMentions().size() < 1) {
				ChannelUtil.sendMessage(channel, user.mention() + " Incorrect arguments. Usage: !Disconnect @User");
				return;
			}
			try {
				boolean createChannel = false;
				for (IUser u : message.getMentions()) {
					if (u.getConnectedVoiceChannels().size() > 0)
						createChannel = true;
				}
				if (!createChannel) {
					ChannelUtil.sendMessage(channel, user.mention() + " No users were able to be removed from their voice channel.");
					return;
				}

				IVoiceChannel temp = message.getGuild().createVoiceChannel(UUID.randomUUID().toString());
				for (IUser u : message.getMentions()) {
					if (u.getConnectedVoiceChannels().size() < 0)
						continue;
					u.moveToVoiceChannel(temp);
				}
				temp.delete();
				ChannelUtil.sendMessage(channel, user.mention() + " Successfully removed users from voice channels.");
			} catch (RateLimitException ex) {
				Logger.error(ExceptionMessage.API_LIMIT);
				Logger.debug(ex);
			} catch (DiscordException ex) {
				Logger.error("Discord Exception: " + ex.getErrorMessage());
				Logger.debug(ex);
			} catch (MissingPermissionsException ex) {
				Logger.error("Unable to delete channel " + channel.getName() + ". Missing Permissions.");
				Logger.debug(ex);
			}
		}
	}

	@EventSubscriber
	public void onReady(ReadyEvent e) {
		Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is READY.");
	}
}
