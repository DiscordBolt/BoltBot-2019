package com.discordbolt.boltbot.utils;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;

/**
 * Created by Techtony96 on 4/4/2017.
 */
public class UserUtil {

    private static IUser botOwner = null;

    public static IUser findUser(IMessage msg, int startIndex) {
        if (msg.getMentions().size() != 0) {
            return msg.getMentions().get(0);
        }

        String name = msg.getContent().substring(startIndex, msg.getContent().length());
        for (IUser user : msg.getGuild().getUsers()) {
            if (user.getName().equalsIgnoreCase(name) || user.getDisplayName(msg.getGuild()).equalsIgnoreCase(name))
                return user;
        }
        return null;
    }

    public static boolean hasPermission(IUser user, IGuild guild, EnumSet<Permissions> permissionss) {
        return user.getPermissionsForGuild(guild).containsAll(permissionss);
    }

    public static boolean hasPermission(IUser user, IGuild guild, Permissions... permissions) {
        for (Permissions p : permissions) {
            if (!user.getPermissionsForGuild(guild).contains(p))
                return false;
        }
        return true;
    }

    public static boolean hasRole(IUser user, IGuild guild, String role) {
        return user.getRolesForGuild(guild).stream().filter(r -> r.getName().equalsIgnoreCase(role)).findAny().isPresent();
    }

    public static boolean isBotOwner(IUser user) {
        if (botOwner == null)
            botOwner = user.getClient().getApplicationOwner();
        return botOwner.equals(user);
    }
}
