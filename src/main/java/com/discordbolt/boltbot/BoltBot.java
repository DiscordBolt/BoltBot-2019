package com.discordbolt.boltbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoltBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoltBot.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Spring application.");
        SpringApplication.run(BoltBot.class, args);
    }
}
