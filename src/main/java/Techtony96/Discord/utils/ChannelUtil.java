package Techtony96.Discord.utils;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class ChannelUtil {

    public static void sendMessage(IChannel channel, String message) {
        if (channel == null || message == null || message.length() == 0)
            return;
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(message);
            } catch (DiscordException ex) {
                Logger.error("Discord Exception: " + ex.getErrorMessage());
                Logger.debug(ex);
            } catch (MissingPermissionsException ex) {
                Logger.error("Unable to send message in channel " + channel.getName() + ". Missing Permissions.");
                Logger.debug(ex);
            }
        });
    }

    public static void sendMessage(IChannel channel, EmbedObject embedObject) {
        if (channel == null)
            return;
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(null, embedObject);
            } catch (DiscordException ex) {
                Logger.error("Discord Exception: " + ex.getErrorMessage());
                Logger.debug(ex);
            } catch (MissingPermissionsException ex) {
                Logger.error("Unable to send message in channel " + channel.getName() + ". Missing Permissions.");
                Logger.debug(ex);
            }
        });
    }
}
