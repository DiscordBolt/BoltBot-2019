package net.ajpappas.discord.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String getFormattedTime(long timestamp) {
        final long totalSeconds = timestamp / 1000;
        final String[] strings = new String[]{"days", "hours", "minutes", "seconds"};
        final long[] data = new long[4];
        data[0] = TimeUnit.SECONDS.toDays(totalSeconds);
        data[1] = TimeUnit.SECONDS.toHours(totalSeconds) - (data[0] * 24);
        data[2] = TimeUnit.SECONDS.toMinutes(totalSeconds) - (TimeUnit.SECONDS.toHours(totalSeconds) * 60);
        data[3] = TimeUnit.SECONDS.toSeconds(totalSeconds) - (TimeUnit.SECONDS.toMinutes(totalSeconds) * 60);

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            long time = data[i];

            if (time > 0) {
                stringBuilder.append(time + " " + (time == 1 ? strings[i].substring(0, strings[i].length() - 1) : strings[i]));

                if (i != data.length - 1) {
                    if (!(i + 2 > (data.length - 1))) {
                        if (data[i + 2] <= 0) {
                            stringBuilder.append(" and ");
                        } else {
                            stringBuilder.append(", ");
                        }
                    } else {
                        if (i == data.length - 2) {
                            stringBuilder.append(" and ");
                        } else {
                            stringBuilder.append(", ");
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
}
