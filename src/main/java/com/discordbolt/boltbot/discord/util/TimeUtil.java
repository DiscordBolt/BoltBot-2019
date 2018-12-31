package com.discordbolt.boltbot.discord.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    /**
     * Turns an instant into a string of X hours, X minutes, and X seconds ago
     *
     * @param instant
     * @return
     */
    public static String timeAgo(Instant instant) {
        long ms = instant.until(Instant.now(), ChronoUnit.MILLIS);
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        ms -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms);
        ms -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);

        StringBuilder string = new StringBuilder();

        if (hours > 0) {
            if (hours == 1)
                string.append("1 hour, ");
            else
                string.append(hours).append(" hours, ");

            if (minutes == 1)
                string.append("1 minute, ");
            else if (minutes > 1)
                string.append(minutes).append(" minutes, ");

            if (seconds == 1)
                string.append("1 second");
            else if (seconds > 1)
                string.append(seconds).append(" seconds");
        } else if (minutes > 0) {
            if (minutes == 1)
                string.append("1 minute, ");
            else if (minutes > 1)
                string.append(minutes).append(" minutes, ");

            if (seconds == 1)
                string.append("1 second");
            else if (seconds > 1)
                string.append(seconds).append(" seconds");
        } else if (seconds > 1) {
            if (seconds == 1)
                string.append("1 second");
            else if (seconds > 1)
                string.append(seconds).append(" seconds");
        } else {
            string.append("less than 1 second");
        }
        return string.toString();
    }
}
