package com.discordbolt.boltbot.utils;

import com.discordbolt.boltbot.BoltBot;
import org.slf4j.LoggerFactory;

public class Logger {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BoltBot.class);

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
