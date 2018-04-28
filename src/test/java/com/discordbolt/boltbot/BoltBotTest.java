package com.discordbolt.boltbot;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoltBotTest {

    @Test
    public void testMain() {
        assertEquals("3.0-SNAPSHOT", BoltBot.getVersion());
    }
}
