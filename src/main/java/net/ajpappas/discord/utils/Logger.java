package net.ajpappas.discord.utils;

public class Logger {

    private static boolean debug = true;
    private static final String COLOR_RESET = "\u001B[0m";
    private static final String COLOR_INFO = "\u001B[36m";
    private static final String COLOR_WARNING = "\u001B[33m";
    private static final String COLOR_ERROR = "\u001B[31m";
    private static final String COLOR_SEVERE = "\u001B[31m";


    public static void debug(Exception e) {
        if (debug) {
            e.printStackTrace();
        }
    }

    public static void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void info(String s) {
        System.out.println(COLOR_INFO + "[Info] " + s + COLOR_RESET);
    }

    public static void warning(String s) {
        System.out.println(COLOR_WARNING + "[Warning] " + s + COLOR_RESET);
    }

    public static void error(String s) {
        System.out.println(COLOR_ERROR + "[ERROR] " + s + COLOR_RESET);
    }

    public static void severe(String s) {
        System.out.println(COLOR_SEVERE + "[SEVERE] " + s + COLOR_RESET);
    }
}

// embed.withFooterText("Requested by: " + user.getName() + "#" + user.getDiscriminator() + " at " + new SimpleDateFormat("EEE MMM d, yyyy 'at' h:mm a").format(new Date()));
