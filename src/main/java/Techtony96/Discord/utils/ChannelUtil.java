package Techtony96.Discord.utils;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class ChannelUtil {

	public static void sendMessage(IChannel channel, String message) {
		try {
			channel.sendMessage(message);
		} catch (RateLimitException | DiscordException | MissingPermissionsException e) {
			Logger.error("Error while trying to send a message");
			Logger.debug(e);
			return;
		}
	}
}
