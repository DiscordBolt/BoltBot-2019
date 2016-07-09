package me.techtony96.modules.tempchannels;

import me.techtony96.utils.ExceptionMessage;
import me.techtony96.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IInvite;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class TemporaryChannel {

	private IUser owner;
	private IVoiceChannel channel;
	private IGuild guild;

	public TemporaryChannel(IUser owner, String name, IGuild guild) {
		this.guild = guild;
		this.owner = owner;

		TempChannelModule.client.getDispatcher().registerListener(this);
		createChannel(name);
	}

	public IUser getOwner() {
		return owner;
	}

	public IChannel getChannel() {
		return channel;
	}
	
	public IInvite getInvite(){
		try {
			return channel.createInvite(0, 0, false);
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

	private void createChannel(String name) {
		try {
			channel = guild.createVoiceChannel(name);
			channel.changeBitrate(96000);
			owner.moveToVoiceChannel(channel);
			TempChannelModule.client.getOrCreatePMChannel(owner).sendMessage("Use https://discord.gg/" + getInvite().getInviteCode() + " to join your voice channel or send it to your friends!");
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

	private void deleteChannel() {
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
		ChannelManager.removeChannel(channel.getID());
	}
	
	private void checkChannel(){
		if (channel.getConnectedUsers().isEmpty()) {
			Logger.debug("Channel is empty. Deleting.");
			deleteChannel();
		}
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
	
	@EventSubscriber
	public void watchChannel(GuildLeaveEvent e){
		checkChannel();
	}
}
