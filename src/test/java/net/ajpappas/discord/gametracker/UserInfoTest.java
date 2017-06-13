package net.ajpappas.discord.gametracker;

import junit.framework.TestCase;
import net.ajpappas.discord.modules.gametracker.UserInfo;

/**
 * Created by Tony on 2/15/2017.
 */
public class UserInfoTest extends TestCase {

    private UserInfo ui1;

    public void setUp() throws Exception {
        super.setUp();
        ui1 = new UserInfo(null, "Overwatch", 567L);
    }

    public void testGetUser() {
        assertNull(ui1.getUser());
    }

    public void testGetGame() {
        assertEquals("Overwatch", ui1.getGame());
    }

    public void testSetGame() {
        ui1.setGame("CS:GO");
        assertEquals("CS:GO", ui1.getGame());
    }

    public void testGetStartTime() {
        assertEquals(567L, ui1.getStartTime());
    }

    public void testSetStartTime() {
        ui1.setStartTime(10L);
        assertEquals(10L, ui1.getStartTime());
    }
}