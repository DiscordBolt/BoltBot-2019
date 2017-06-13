package net.ajpappas.discord.api.list;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tony on 5/13/2017.
 */
public class ArgListTest {

    private ArgList list;

    @Before
    public void setUp() throws Exception {
        list = new ArgList(new String[]{"delete", "UPDATE", "SeT"});
    }

    @Test
    public void containsIgnoreCase() {
        assertTrue(list.containsIgnoreCase("Delete"));
        assertTrue(list.containsIgnoreCase("set"));
        assertTrue(list.containsIgnoreCase("UPDATE"));

        assertFalse(list.containsIgnoreCase("delete "));
        assertFalse(list.containsIgnoreCase(" delete"));
    }
}