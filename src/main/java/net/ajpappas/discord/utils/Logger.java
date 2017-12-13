package net.ajpappas.discord.utils;

import net.ajpappas.discord.Discord;
import org.slf4j.LoggerFactory;

public class Logger {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Discord.class);

    public static void trace(String s) {
        LOGGER.trace(s);
    }

    public static void debug(Throwable e) {
        LOGGER.debug(e.getMessage(), e);
    }

    public static void debug(String s) {
        LOGGER.debug(s);
    }

    public static void info(String s) {
        LOGGER.info(s);
    }

    public static void warning(String s) {
        LOGGER.warn(s);
    }

    public static void error(String s) {
        LOGGER.error(s);
    }
}
