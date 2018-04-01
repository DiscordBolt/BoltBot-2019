package com.discordbolt.boltbot.modules.twitch;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.system.mysql.data.persistent.GuildData;
import com.discordbolt.boltbot.system.mysql.data.persistent.UserData;
import com.discordbolt.boltbot.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tony on 12/24/2016.
 */
public class StreamAnnouncer {

    private IDiscordClient client;

    private ArrayList<Streamer> streamers = new ArrayList<>();

    public StreamAnnouncer(IDiscordClient client) {
        this.client = client;
    }

    @EventSubscriber
    public void onUserGameUpdate(PresenceUpdateEvent e) {
        if (e.getNewPresence().getStreamingUrl().isPresent()) {
            if (!UserData.getOrCreate(e.getUser()).shouldAnnounceStreamerStatus())
                return;
            for (Streamer s : streamers) {
                if (s.getStreamer().equals(e.getUser())) {
                    if (s.isTimePassed(24, TimeUnit.HOURS) || s.isTimeAfterElapsed(1, TimeUnit.HOURS)) {
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

    private void sendAnnouncement(PresenceUpdateEvent e) {
        for (IGuild guild : client.getGuilds()) {
            long channelID = GuildData.getById(guild.getLongID()).get().getStreamAnnounceChannel();
            if (channelID == 0L)
                continue;
            if (!guild.getUsers().contains(e.getUser()))
                continue;

            ChannelUtil.sendMessage(guild.getChannelByID(channelID), e.getUser().mention() + " just started streaming " + e.getNewPresence().getText().orElse("") + "\nCome join in on the fun! <" + e.getNewPresence().getStreamingUrl().orElse("") + ">");
        }
    }

    @BotCommand(command = {"twitch", "announce"}, description = "Enable or disable announcing when you go live.", usage = "Twitch announce true/false", module = "AAAAAAAAAA", args = 3)
    public static void onCommand(CommandContext cc) throws CommandException {
        if (cc.getArgument(2).equalsIgnoreCase("true")) {
            UserData.getOrCreate(cc.getAuthor()).setAnnounceStreamingStatus(true);
            cc.replyWith("Enabled announcing of your stream!");
        } else if (cc.getArgument(2).equalsIgnoreCase("false")) {
            UserData.getOrCreate(cc.getAuthor()).setAnnounceStreamingStatus(false);
            cc.replyWith("Disabled annoucing your stream.");
        } else {
            throw new CommandArgumentException("Third argument must be `true` or `false`");
        }
    }

    /**
     * Created by Tony on 1/21/2017.
     */
    public static class Streamer {

        private IUser streamer;
        private long startTime, endTime = 0L;

        public Streamer(IUser streamer) {
            this.streamer = streamer;
            this.startTime = System.currentTimeMillis();
        }

        public boolean isTimePassed(long duration, TimeUnit source) {
            return startTime + TimeUnit.MILLISECONDS.convert(duration, source) <= System.currentTimeMillis();
        }

        public boolean isTimeAfterElapsed(long duration, TimeUnit source) {
            if (endTime == 0L) {
                return false;
            }

            return startTime + TimeUnit.MILLISECONDS.convert(duration, source) <= System.currentTimeMillis();
        }

        public IUser getStreamer() {
            return streamer;
        }

        public void setStreamer(IUser streamer) {
            this.streamer = streamer;
        }
    }
}
