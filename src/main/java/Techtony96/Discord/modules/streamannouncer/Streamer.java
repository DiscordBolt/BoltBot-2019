package Techtony96.Discord.modules.streamannouncer;

import sx.blah.discord.handle.obj.IUser;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tony on 1/21/2017.
 */
public class Streamer {

    private IUser streamer;
    private long startTime, endTime = 0L;

    public Streamer(IUser streamer) {
        this.streamer = streamer;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isTimePassed(long duration, TimeUnit source) {
        long ms = TimeUnit.MILLISECONDS.convert(duration, source);
        if (startTime + ms <= System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    public boolean isTimeAfterElapsed(long duration, TimeUnit source) {
        if (endTime == 0L) {
            return false;
        }

        long ms = TimeUnit.MILLISECONDS.convert(duration, source);
        if (startTime + ms <= System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    public IUser getStreamer() {
        return streamer;
    }

    public void setStreamer(IUser streamer) {
        this.streamer = streamer;
    }
}
