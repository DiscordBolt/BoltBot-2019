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

/**
 * Created by Tony on 4/3/2017.
 */
public class SeenModule extends CustomModule implements IModule {

    public SeenModule() {
        super("Seen Module", "1.0");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        // Update all users
        updateUsers();

        new BotCommand(client, "seen") {
            @Override
            public void execute(CommandContext cc) {

                IUser searchUser = UserUtil.findUser(cc.getMessage(), cc.getContent().indexOf(' ') + 1);

                if (searchUser == null) {
                    String name = cc.getContent().substring(cc.getContent().indexOf(' ') + 1, cc.getContent().length());
                    cc.replyWith("Sorry, I could not find \"" + name + "\".");
                    return;
                }

                StatusType status = searchUser.getPresence().getStatus();
                try {
                    cc.replyWith(searchUser.getName() + " has been " + status.name().toLowerCase().replace("dnd", "do not disturb") + " since " + format(getLastChange(searchUser)) + '.');
                    return;
                } catch (IllegalStateException e) {
                    cc.replyWith("An error has occurred while processing your command. Please try again later.");
                    return;
                }
            }
        }.setUsage("!Seen @User").setDescription("See when the user was last online.").setArguments(2, 100);
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
            ps.setString(1, user.getID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
            throw new IllegalStateException("The MySQL Query could not be completed.", e);
        }
    }

    private void updateUsers() {
        for (IGuild guild : client.getGuilds()) {
            for (IUser user : guild.getUsers()) {
                updateUser(user);
            }
        }
    }

    private String format(Timestamp time) {
        PrettyTime p = new PrettyTime();
        return p.format(time);
    }

    private Timestamp getLastChange(IUser user) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT `lastUpdate` FROM `seen` WHERE `user` = ?;");
            ps.setString(1, user.getID());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getTimestamp(1);
            else
                throw new IllegalStateException("The MySQL Query could not be completed.");
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
            throw new IllegalStateException("The MySQL Query could not be completed.", e);
        }
    }
}
