package net.ajpappas.discord.utils;

import sx.blah.discord.Discord4J;

public class Logger {

    public static void debug(Throwable e) {
        Discord4J.LOGGER.debug(e.getMessage(), e);
    }

    public static void debug(String s) {
        Discord4J.LOGGER.debug(s);
    }

    public static void info(String s) {
        Discord4J.LOGGER.info(s);
    }

    public static void warning(String s) {
        Discord4J.LOGGER.warn(s);
    }

    public static void error(String s) {
        Discord4J.LOGGER.error(s);
    }
}
