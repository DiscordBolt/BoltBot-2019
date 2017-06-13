package net.ajpappas.discord.utils;

import com.vdurmont.emoji.Emoji;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelUtil {

    public static IMessage sendMessage(IChannel channel, String message) {
        if (channel == null || message == null || message.length() == 0)
            return null;
        return RequestBuffer.request(() -> {
            try {
                return channel.sendMessage(message);
            } catch (DiscordException ex) {
                Logger.error("Discord Exception: " + ex.getErrorMessage());
                Logger.debug(ex);
                return null;
            } catch (MissingPermissionsException e) {
                Logger.error("Unable to send message in channel " + channel.getName() + ". Missing Permissions.");
                Logger.debug(e);
                return null;
            }
        }).get();
    }

    public static IMessage sendMessage(IChannel channel, EmbedObject embedObject) {
        if (channel == null || embedObject == null)
            return null;

        return RequestBuffer.request(() -> {
            try {
                return channel.sendMessage(embedObject);
            } catch (DiscordException ex) {
                Logger.error("Discord Exception: " + ex.getErrorMessage());
                Logger.debug(ex);
                return null;
            } catch (MissingPermissionsException e) {
                Logger.error("Unable to send message in channel " + channel.getName() + ". Missing Permissions.");
                Logger.debug(e);
                return null;
            }
        }).get();
    }

    /**
     * Adds a reaction to the message
     *
     * @param m   The message to add to
     * @param es2 The reaction list to add
     */
    public static void addReaction(IMessage m, Emoji[] es2) {
        List<Emoji> es1 = Arrays.asList(es2);
        Collections.reverse(es1);
        Emoji[] es = (Emoji[]) es1.toArray();
        final AtomicInteger i = new AtomicInteger();
        RequestBuffer.request(() -> {
            for (; i.get() < es.length; i.incrementAndGet()) {
                if (es[i.intValue()] != null) {
                    m.addReaction(es[i.intValue()]);
                }
            }

        });
    }

    public static void addReaction(IMessage message, String emoji) {
        if (message == null || emoji == null || emoji.length() == 0)
            return;
        RequestBuffer.request(() -> message.addReaction(emoji));
    }

}
