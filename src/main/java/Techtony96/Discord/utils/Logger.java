package Techtony96.Discord.utils;

import java.sql.SQLException;

public class Logger {

    private static boolean debug = true;

    public static void debug(Exception e) {
        if (debug && !(e instanceof SQLException)) {
            e.printStackTrace();
        }
    }

    public static void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void info(String s) {
        System.out.println("[Info] " + s);
    }

    public static void warning(String s) {
        System.out.println("[Warning] " + s);
    }

    public static void error(String s) {
        System.out.println("[ERROR] " + s);
    }

    public static void severe(String s) {
        System.out.println("[SEVERE] " + s);
    }
}

// embed.withFooterText("Requested by: " + user.getName() + "#" + user.getDiscriminator() + " at " + new SimpleDateFormat("EEE MMM d, yyyy 'at' h:mm a").format(new Date()));
