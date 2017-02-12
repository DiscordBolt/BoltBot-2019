package Techtony96.Discord.modules.streamannouncer;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.ChannelUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.modules.IModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tony on 12/24/2016.
 */
public class StreamAnnouncer extends CustomModule implements IModule {

    private static final String[] CHANNELS = {"announcements", "general"};

    private ArrayList<Streamer> streamers = new ArrayList<>();

    public StreamAnnouncer() {
        super("Stream Announcer", "1.0");
    }

    @EventSubscriber
    public void onUserGameUpdate(StatusChangeEvent e) {
        if (e.getNewStatus().getType() == Status.StatusType.STREAM) {
            for (Streamer s : streamers){
                if (s.getStreamer().getID().equals(e.getUser().getID())){
                    if (s.isTimePassed(2, TimeUnit.HOURS) || s.isTimeAfterElapsed(1, TimeUnit.HOURS)){
                       sendAnnouncement(e);
                       streamers.remove(s);
                       streamers.add(new Streamer(e.getUser()));
                       return;
                    } else
                        return;
                }
            }
            sendAnnouncement(e);
            streamers.add(new Streamer(e.getUser()));
        }
    }

    private void sendAnnouncement(StatusChangeEvent e){
        List<IGuild> guilds = client.getGuilds();
        for (IGuild guild : guilds){
            if (!guild.getUsers().contains(e.getUser()))
                guilds.remove(guild);
        }
        for (IGuild guild : guilds){
            for (String channel : CHANNELS){
                List<IChannel> channels = guild.getChannelsByName(channel);
                if (channels.size() > 0){
                    ChannelUtil.sendMessage(channels.get(0), e.getUser().mention() + " just started streaming " + e.getNewStatus().getStatusMessage());
                    ChannelUtil.sendMessage(channels.get(0), "Come join in on the fun! " + e.getUser().getStatus().getUrl().orElseGet(null));
                    return;
                }
            }
        }
    }
}
