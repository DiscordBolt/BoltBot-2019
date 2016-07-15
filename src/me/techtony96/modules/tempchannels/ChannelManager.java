package me.techtony96.modules.tempchannels;

import java.util.ArrayList;
import java.util.List;

import me.techtony96.modules.tempchannels.exceptions.DuplicateChannelException;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class ChannelManager {
	
	private static List<TemporaryChannel> list = new ArrayList<>();
	
	/**
	 * Create a temporary channel
	 * @param owner User of the temporary channel
	 * @param name Name of the channel
	 * @param guild Guild to create the voice channel in
	 * @param privateChannel Boolean if channel should be locked to outside users
	 * @throws DuplicateChannelException if owner already owns a private channel
	 */
	public static void createChannel(IUser owner, String name, IGuild guild, boolean privateChannel) throws DuplicateChannelException{
		for (TemporaryChannel ch : list){
			if (ch.getOwner().getID().equals(owner.getID())){
				throw new DuplicateChannelException();
			}
		}
		TemporaryChannel tChannel = new TemporaryChannel(owner, name, guild, privateChannel);
		list.add(tChannel);
	}
	
	/**
	 * Remove a temporary channel
	 * @param ID ID of voice channel to remove
	 * @return Boolean removed channel
	 */
	public static boolean removeChannel(String ID){
		for (TemporaryChannel channel : list){
			if (channel.getChannel().getID().equals(ID)){
				return removeChannel(channel);
			}
		}
		return false;
	}
	
	/**
	 * Remove a temporary channel
	 * @param channel
	 * @return
	 */
	public static boolean removeChannel(TemporaryChannel channel){
		channel.deleteChannel();
		return list.remove(channel);
	}
	
	/**
	 * Get the TemporaryChannel object of a given Owner
	 * @param owner of channel requested
	 * @return TemporaryChannel
	 */
	public static TemporaryChannel getChannel(IUser owner){
		for (TemporaryChannel channel : list){
			if (channel.getOwner().equals(owner)){
				return channel;
			}
		}
		return null;
	}
	
	

}
