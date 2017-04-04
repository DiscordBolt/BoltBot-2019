package Techtony96.Discord.utils;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Techtony96 on 4/4/2017.
 */
public class UserUtil {

    public static IUser findUser(IMessage msg, int startIndex) {
        if (msg.getMentions().size() != 0) {
            return msg.getMentions().get(0);
        }

        String name = msg.getContent().substring(startIndex, msg.getContent().length());
        for (IUser user : msg.getGuild().getUsers()){
            if (user.getName().equalsIgnoreCase(name) || user.getDisplayName(msg.getGuild()).equalsIgnoreCase(name))
                return user;
        }

        return null;
    }
}
