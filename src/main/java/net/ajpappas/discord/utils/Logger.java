package net.ajpappas.discord.utils;

import sx.blah.discord.Discord4J;

public class Logger {

    private static final String COLOR_RESET = "\u001B[0m";
    private static final String COLOR_INFO = "\u001B[36m";
    private static final String COLOR_WARNING = "\u001B[33m";
    private static final String COLOR_ERROR = "\u001B[31m";

    public static void debug(Exception e) {
        Discord4J.LOGGER.debug(e.getMessage(), e);
    }

    public static void debug(String s) {
        Discord4J.LOGGER.debug(s);
    }

    public static void info(String s) {
        Discord4J.LOGGER.info(COLOR_INFO + s + COLOR_RESET);
    }

    public static void warning(String s) {
        Discord4J.LOGGER.warn(COLOR_WARNING + s + COLOR_RESET);
    }

    public static void error(String s) {
        Discord4J.LOGGER.error(COLOR_ERROR + s + COLOR_RESET);
    }
}
