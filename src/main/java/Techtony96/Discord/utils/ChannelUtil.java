package Techtony96.Discord.utils;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.concurrent.Semaphore;

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
        if (channel == null || embedObject == null)
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

    public static IMessage sendMessageWait(IChannel channel, String message){
        if (channel == null || message == null || message.length() == 0)
            return null;
        Semaphore sem = new Semaphore(0);
        final IMessage[] sentMessage = new IMessage[1];
        RequestBuffer.request(() -> {
            try {
                sentMessage[0] = channel.sendMessage(message);
                sem.release();
            } catch (DiscordException ex) {
                Logger.error("Discord Exception: " + ex.getErrorMessage());
                Logger.debug(ex);
            } catch (MissingPermissionsException ex) {
                Logger.error("Unable to send message in channel " + channel.getName() + ". Missing Permissions.");
                Logger.debug(ex);
            }
        });
        try {
            sem.acquire();
        } catch (InterruptedException e) {
        }
        return sentMessage[0];
    }

    public static IMessage sendMessageWait(IChannel channel, EmbedObject embedObject){
        if (channel == null || embedObject == null)
            return null;
        Semaphore sem = new Semaphore(0);
        final IMessage[] sentMessage = new IMessage[1];
        RequestBuffer.request(() -> {
            try {
                sentMessage[0] = channel.sendMessage(null, embedObject);
                sem.release();
            } catch (DiscordException ex) {
                Logger.error("Discord Exception: " + ex.getErrorMessage());
                Logger.debug(ex);
            } catch (MissingPermissionsException ex) {
                Logger.error("Unable to send message in channel " + channel.getName() + ". Missing Permissions.");
                Logger.debug(ex);
            }
        });
        try {
            sem.acquire();
        } catch (InterruptedException e) {
        }
        return sentMessage[0];
    }
}
