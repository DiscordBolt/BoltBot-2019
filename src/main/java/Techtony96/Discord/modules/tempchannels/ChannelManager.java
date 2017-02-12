package Techtony96.Discord.modules.tempchannels;

import Techtony96.Discord.modules.tempchannels.exceptions.DuplicateChannelException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

	private static List<TemporaryChannel> list = new ArrayList<>();

	/**
	 * Create a temporary channel
	 *
	 * @param owner          User of the temporary channel
	 * @param name           Name of the channel
	 * @param guild          Guild to create the voice channel in
	 * @param privateChannel Boolean if channel should be locked to outside users
	 * @throws DuplicateChannelException if owner already owns a private channel
	 */
	public static void createChannel(IDiscordClient client, IUser owner, String name, IGuild guild, boolean privateChannel) throws DuplicateChannelException {
		for (TemporaryChannel tc : list) {
			if (tc.getOwner().equals(owner))
				throw new DuplicateChannelException();
		}
		list.add(new TemporaryChannel(client, owner, name, guild, privateChannel));
	}

	/**
	 * Get the TemporaryChannel object of a given Owner
	 *
	 * @param owner of channel requested
	 * @return TemporaryChannel of owner, null if IUser doesn't own a channel
	 */
	public static TemporaryChannel getChannel(IUser owner) {
		for (TemporaryChannel channel : list) {
			if (channel.getOwner().equals(owner))
				return channel;
		}
		return null;
	}

	/**
	 * Remove a temporary channel
	 *
	 * @param ID of voice channel to remove
	 * @return boolean removed channel
	 */
	public static boolean removeChannel(String ID) {
		for (TemporaryChannel tc : list) {
			if (tc.getChannel().getID().equals(ID))
				return removeChannel(tc);
		}
		return false;
	}

	/**
	 * Remove a temporary channel
	 *
	 * @param channel TemporaryChannel Object
	 * @return boolean removed channel
	 */
	public static boolean removeChannel(TemporaryChannel channel) {
		channel.deleteChannel();
		return list.remove(channel);
	}
}
