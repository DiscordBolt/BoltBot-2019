package Techtony96.Discord.utils;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class ChannelUtil {

	public static void sendMessage(IChannel channel, String message) {
		try {
			channel.sendMessage(message);
		} catch (RateLimitException ex) {
			Logger.error(ExceptionMessage.API_LIMIT);
			Logger.debug(ex);
		} catch (DiscordException ex) {
			Logger.error("Discord Exception: " + ex.getErrorMessage());
			Logger.debug(ex);
		} catch (MissingPermissionsException ex) {
			Logger.error("Unable to send message in channel " + channel.getName() + ". Missing Permissions.");
			Logger.debug(ex);
		}
	}
}
