package Techtony96.Discord.modules.tempchannels;

import Techtony96.Discord.utils.ExceptionMessage;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.EnumSet;

public class TemporaryChannel {

	private IDiscordClient client;
	private IUser owner;
	private IVoiceChannel channel;
	private IGuild guild;
	private IInvite invite;
	private boolean privateChannel;

	public TemporaryChannel(IDiscordClient client, IUser owner, String name, IGuild guild, boolean privateChannel) {
		this.client = client;
		this.guild = guild;
		this.owner = owner;
		this.privateChannel = privateChannel;

		createChannel(name);

		client.getDispatcher().registerListener(this);

		// Create a timer to check if the channel is still empty in one minute
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				checkChannel();
			}
		}, 60000);
	}

	private void checkChannel() {
		if (channel.getConnectedUsers().isEmpty()) {
			ChannelManager.removeChannel(this);
		}
	}

	/**
	 * Create a voice channel in the guild
	 *
	 * @param name of new voice channel
	 */
	private void createChannel(String name) {
		try {
			channel = guild.createVoiceChannel(name);
			channel.changeBitrate(96000);
			client.getOrCreatePMChannel(owner).sendMessage("Use https://discord.gg/" + getInvite().getInviteCode() + " to join your voice channel or send it to your friends!");
			if (privateChannel) {
				lockChannel();
				client.getOrCreatePMChannel(owner).sendMessage("Use !Add @User to give a user permission to join your voice channel!");
			}
			owner.moveToVoiceChannel(channel);
		} catch (RateLimitException e) {
			Logger.error(ExceptionMessage.API_LIMIT);
			Logger.debug(e);
		} catch (DiscordException e) {
			Logger.error("Discord Exception: " + e.getErrorMessage());
			Logger.debug(e);
		} catch (MissingPermissionsException e) {
			Logger.error("Error while creating channel " + name + ". Missing Permissions.");
			Logger.debug(e);
		}
	}

	private IInvite createInvite() {
		try {
			return channel.createInvite(0, 0, false, false);
		} catch (RateLimitException e) {
			Logger.error(ExceptionMessage.API_LIMIT);
			Logger.debug(e);
		} catch (MissingPermissionsException e) {
			Logger.error("Missing permissions to create invite link.");
			Logger.debug(e);
		} catch (DiscordException e) {
			Logger.error("Discord Exception: " + e.getErrorMessage());
			Logger.debug(e);
		}
		return null;
	}

	/**
	 * Remove channel from Guild
	 */
	public void deleteChannel() {
		try {
			channel.delete();
		} catch (RateLimitException e) {
			Logger.error(ExceptionMessage.API_LIMIT);
			Logger.debug(e);
		} catch (DiscordException e) {
			Logger.error("Discord Exception: " + e.getErrorMessage());
			Logger.debug(e);
		} catch (MissingPermissionsException e) {
			Logger.error("Unable to delete channel " + channel.getName() + ". Missing Permissions.");
			Logger.debug(e);
		}
	}

	public IChannel getChannel() {
		return channel;
	}

	public IInvite getInvite() {
		if (invite == null) {
			invite = createInvite();
		}
		return invite;
	}

	public IUser getOwner() {
		return owner;
	}

	/**
	 * Give a user access to the voice channel
	 *
	 * @param user
	 */
	public void giveUserPermission(IUser user) {
		try {
			channel.overrideUserPermissions(user, EnumSet.of(Permissions.VOICE_CONNECT), EnumSet.noneOf(Permissions.class));
			client.getOrCreatePMChannel(user).sendMessage(owner.mention() + " has given you permission to join " + channel.getName() + " / " + guild.getName());
		} catch (RateLimitException e) {
			Logger.error(ExceptionMessage.API_LIMIT);
			Logger.debug(e);
		} catch (DiscordException e) {
			Logger.error("Discord Exception: " + e.getErrorMessage());
			Logger.debug(e);
		} catch (MissingPermissionsException e) {
			Logger.error("Unable to delete channel " + channel.getName() + ". Missing Permissions.");
			Logger.debug(e);
		}
	}

	public boolean isPrivate() {
		return privateChannel;
	}

	/**
	 * Make a channel private
	 */
	private void lockChannel() {
		try {
			channel.overrideRolePermissions(guild.getEveryoneRole(), EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.VOICE_CONNECT));
			channel.overrideUserPermissions(owner, EnumSet.of(Permissions.VOICE_CONNECT), EnumSet.noneOf(Permissions.class));
		} catch (RateLimitException e) {
			Logger.error(ExceptionMessage.API_LIMIT);
			Logger.debug(e);
		} catch (DiscordException e) {
			Logger.error("Discord Exception: " + e.getErrorMessage());
			Logger.debug(e);
		} catch (MissingPermissionsException e) {
			Logger.error("Unable to delete channel " + channel.getName() + ". Missing Permissions.");
			Logger.debug(e);
		}
	}

	@EventSubscriber
	public void watchChannel(GuildLeaveEvent e) {
		checkChannel();
	}

	@EventSubscriber
	public void watchChannel(UserVoiceChannelLeaveEvent e) {
		if (e.getChannel().getID().equals(channel.getID())) {
			checkChannel();
		}
	}

	@EventSubscriber
	public void watchChannel(UserVoiceChannelMoveEvent e) {
		if (e.getOldChannel().getID().equals(channel.getID())) {
			checkChannel();
		}
	}
}
