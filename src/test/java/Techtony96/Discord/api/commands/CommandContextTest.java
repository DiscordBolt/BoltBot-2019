package Techtony96.Discord.api.commands;

import mock.MockMessage;
import mock.MockObjects;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Techt on 5/2/2017.
 */
public class CommandContextTest {

    private CommandContext cc, ccArgTest;

    @Before

    public void setup() {
        cc = new CommandContext(null, MockObjects.mockCommand);
        ccArgTest = new CommandContext(null, new MockMessage("!Command arg1 arg2", 5, MockObjects.mockUser, MockObjects.mockChannel));
    }

    @Test
    public void testGetMessage() {
        assertNotNull(cc.getMessage());
        assertEquals(MockObjects.mockCommand, cc.getMessage());
    }

    @Test
    public void testGetUser() {
        assertNotNull(cc.getUser());
        assertEquals(MockObjects.mockUser, cc.getUser());
    }

    @Test
    public void testGetGuild() {
        assertNotNull(cc.getGuild());
        assertEquals(MockObjects.mockGuild, cc.getGuild());
    }

    @Test
    public void testMentionUser() {
        assertNotNull(cc.mentionUser());
        assertEquals(MockObjects.mockUser.mention(), cc.mentionUser());
    }

    @Test
    public void testGetUserDisplayName() {
        assertNotNull(cc.getUserDisplayName());
        assertEquals(MockObjects.mockUser.getDisplayName(MockObjects.mockGuild), cc.getUserDisplayName());
    }

    @Test
    public void testGetMentions() {

    }

    @Test
    public void testIsPrivateMessage() {

    }

    @Test
    public void testGetContent() {
        assertNotNull(cc.getContent());
        assertEquals(MockObjects.mockCommand.getContent(), cc.getContent());
    }

    @Test
    public void testGetCommand() {
        assertNotNull(cc.getCommand());
        assertEquals(MockObjects.mockCommand.getContent().split(" ")[0].replace("!", ""), cc.getCommand());
    }

    @Test
    public void testGetArgument() {
        assertEquals("Command", ccArgTest.getArgument(0));
        assertEquals("arg1", ccArgTest.getArgument(1));
        assertEquals("arg2", ccArgTest.getArgument(2));
    }

    @Test
    public void testCombineArgs() {
        assertEquals("arg1 arg2", ccArgTest.combineArgs(1, 2));
        assertEquals("Command arg1 arg2", ccArgTest.combineArgs(0, 2));
        assertEquals("Command arg1", ccArgTest.combineArgs(0, 1));
    }

    @Test
    public void testReplyWith() {

    }

    @Test
    public void testSendUsage() {

    }
}
