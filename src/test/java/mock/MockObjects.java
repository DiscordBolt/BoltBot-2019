package mock;

import sx.blah.discord.handle.impl.obj.Presence;
import sx.blah.discord.handle.obj.StatusType;

/**
 * Created by Techt on 5/2/2017.
 */
public class MockObjects {

    public static final MockUser mockUser = new MockUser("Mock User", 1, new Presence(null, null, StatusType.ONLINE));
    public static final MockGuild mockGuild = new MockGuild("Mock Guild", 2, mockUser.getLongID());
    public static final MockChannel mockChannel = new MockChannel("Mock Channel", 3, mockGuild);
    // public static final MockMessage mockMessage = new MockMessage("Message Content", 4, mockUser, mockChannel);
    public static final MockMessage mockCommand = new MockMessage("!Command", 5, mockUser, mockChannel);
}
