package Techtony96.Discord.modules.log;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.VoiceChannel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.modules.IModule;

import java.util.HashMap;

/**
 * Created by Tony on 12/24/2016.
 */
public class LogModule extends CustomModule implements IModule {

    private static final String LOG_CHANNEL = "bot-log";
    private static HashMap<IGuild, LogListener> listeners = new HashMap<>();


    public LogModule() {
        super("Log", "1.0");
    }

    @EventSubscriber
    public void readyEvent(ReadyEvent e) {
        for (IGuild guild : client.getGuilds()){
            for (IChannel channel : guild.getChannels()){
                if (channel instanceof VoiceChannel)
                    continue;
                if (channel.getName().equalsIgnoreCase(LOG_CHANNEL)){
                    listeners.put(guild, new LogListener(client, guild, channel));
                    break;
                }
            }
            if (!listeners.containsKey(guild))
                Logger.warning("No logging channel found for " + guild.getName());
        }
    }


    public static void logMessage(IGuild guild, String s){
        if (listeners.containsKey(guild)){
            listeners.get(guild).log(s);
        }
    }
}
