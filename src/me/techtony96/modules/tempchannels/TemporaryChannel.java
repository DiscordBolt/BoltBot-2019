package me.techtony96.modules.tempchannels;

import me.techtony96.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class TemporaryChannel {

	private IUser owner;
	private IVoiceChannel channel;
	private IGuild guild;
	private IDiscordClient client = TempChannelModule.client;

	public TemporaryChannel(IUser owner, String name, IGuild guild) {
		this.guild = guild;
		this.owner = owner;

		client.getDispatcher().registerListener(this);
		createChannel(name);
	}

	public IUser getOwner() {
		return owner;
	}

	public IChannel getChannel() {
		return channel;
	}

	private void createChannel(String name) {
		try {
			channel = guild.createVoiceChannel(name);
			channel.changeBitrate(96000);
			owner.moveToVoiceChannel(channel);
		} catch (RateLimitException e) {
			Logger.error("Sending Discord too many requests. Rate limit hit.");
			Logger.debug(e);
		} catch (DiscordException e) {
			Logger.error("Discord Exception: " + e.getErrorMessage());
			Logger.debug(e);
		} catch (MissingPermissionsException e) {
			Logger.error("Unable to create channel " + name + ". Missing Permissions.");
			Logger.debug(e);
		}
	}

	private void deleteChannel() {
		try {
			channel.delete();
		} catch (RateLimitException e) {
			Logger.error("Sending Discord too many requests. Rate limit hit.");
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

	@EventSubscriber
	public void watchChannel(UserVoiceChannelLeaveEvent e) {
		System.out.println("User left voice channel.");
		if (!e.getChannel().getID().equals(channel.getID())) {
			return;
		}
		Logger.debug("Our voice channel was left");
		if (channel.getConnectedUsers().isEmpty()) {
			Logger.debug("Channel is empty. Deleting.");
			deleteChannel();
		}
	}
	
	@EventSubscriber
	public void testEvent(MessageReceivedEvent e){
		try {
			e.getMessage().getChannel().sendMessage("Saw your message!");
		} catch (RateLimitException | MissingPermissionsException | DiscordException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
