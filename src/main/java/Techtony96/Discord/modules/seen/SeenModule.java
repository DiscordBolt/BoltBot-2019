package Techtony96.Discord.modules.seen;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.mysql.MySQL;
import Techtony96.Discord.utils.Logger;
import Techtony96.Discord.utils.UserUtil;
import org.ocpsoft.prettytime.PrettyTime;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.modules.IModule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Tony on 4/3/2017.
 */
public class SeenModule extends CustomModule implements IModule {

    public SeenModule() {
        super("Seen Module", "1.0");
    }

    @BotCommand(command = "seen", module = "Seen Module", description = "See when the user was last online.", usage = "!Seen @User", minArgs = 2, maxArgs = 100)
    public static void seenCommand(CommandContext cc) {

        IUser searchUser = UserUtil.findUser(cc.getMessage(), cc.getContent().indexOf(' ') + 1);
        String name = cc.getContent().substring(cc.getContent().indexOf(' ') + 1, cc.getContent().length());

        if (searchUser == null) {
            cc.replyWith("Sorry, I could not find \"" + name + "\".");
            return;
        }

        StatusType status = searchUser.getPresence().getStatus();
        Timestamp lastUpate = getLastChange(searchUser);
        if (lastUpate == null) {
            cc.replyWith("Sorry, I could not find \"" + name + "\".");
            return;
        }

        cc.replyWith(searchUser.getName() + " has been " + status.name().toLowerCase().replace("dnd", "do not disturb") + " since " + format(lastUpate) + '.');
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        updateAllUsers();
    }

    @EventSubscriber
    public void onStatusChange(PresenceUpdateEvent e) {
        if (e.getNewPresence().getStatus() != e.getOldPresence().getStatus()) {
            // Update database
            updateUser(e.getUser());
        }
    }

    private int updateUser(IUser user) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("REPLACE INTO `seen` (`user`, `lastUpdate`) VALUES (?,NOW());");
            ps.setLong(1, user.getLongID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Unable to update " + user.getName() + "'s presense in the database.");
            Logger.debug(e);
            return -1;
        }
    }

    private int[] updateAllUsers() {
        Set<IUser> users = new HashSet<>();
        client.getGuilds().forEach(g -> users.addAll(g.getUsers()));
        Iterator<IUser> it = users.iterator();
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("REPLACE INTO `seen` (`user`, `lastUpdate`) VALUES (?,NOW());");
            while (it.hasNext()) {
                ps.setLong(1, it.next().getLongID());
                ps.addBatch();
                it.remove();
            }
            return ps.executeBatch();
        } catch (SQLException e) {
            Logger.error("Unable to update all users presense in the database.");
            Logger.debug(e);
            return new int[]{-1};
        }
    }

    private void updateUsers() {
        for (IGuild guild : client.getGuilds()) {
            for (IUser user : guild.getUsers())
                updateUser(user);
        }
    }

    private static String format(Timestamp time) {
        PrettyTime p = new PrettyTime();
        return p.format(time);
    }

    private static Timestamp getLastChange(IUser user) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT `lastUpdate` FROM `seen` WHERE `user` = ?;");
            ps.setLong(1, user.getLongID());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getTimestamp(1);
        } catch (SQLException e) {
            Logger.error("Unable to fetch " + user.getName() + "'s last presense change from the database.");
            Logger.debug(e);
        }
        return null;
    }
}
