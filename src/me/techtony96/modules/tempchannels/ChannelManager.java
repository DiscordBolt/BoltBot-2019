package me.techtony96.modules.tempchannels;

import java.util.ArrayList;
import java.util.List;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class ChannelManager {
	
	private static List<TemporaryChannel> list = new ArrayList<>();
	
	public static void createChannel(IUser owner, String name, IGuild guild){
		TemporaryChannel tChannel = new TemporaryChannel(owner, name, guild);
		list.add(tChannel);
	}
	
	public static boolean removeChannel(String ID){
		for (TemporaryChannel channel : list){
			if (channel.getChannel().getID().equals(ID)){
				list.remove(channel);
				return true;
			}
		}
		return false;
	}
	
	

}
