package net.ajpappas.discord.modules.seen;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.utils.UserUtil;
import org.ocpsoft.prettytime.PrettyTime;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by Tony on 4/3/2017.
 */
public class SeenModule extends CustomModule implements IModule {

    private static HashMap<Long, UserStatus> statuses;

    public SeenModule() {
        super("Seen Module", "1.1");
        statuses = StatusFileIO.loadStatuses();
    }

    @BotCommand(command = "seen", module = "Seen Module", description = "See when the user was last online.", usage = "Seen @User", minArgs = 2, maxArgs = 100)
    public static void seenCommand(CommandContext cc) {

        IUser searchUser = UserUtil.findUser(cc.getMessage(), cc.getContent().indexOf(' ') + 1);
        String name = cc.getContent().substring(cc.getContent().indexOf(' ') + 1, cc.getContent().length());

        if (searchUser == null) {
            cc.replyWith("Sorry, I could not find \"" + name + "\".");
            return;
        }

        UserStatus status = statuses.get(searchUser.getLongID());
        if (status == null) {
            cc.replyWith("Sorry, I could not find \"" + name + "\".");
            return;
        }

        cc.replyWith(searchUser.getName() + " has been " + status.getStatus().name().replace("dnd", "do not disturb") + " since " + format(status.getLastUpdate()) + '.');
    }

    // This has been taken to GuildCreateEvent
    //@EventSubscriber
    //public void onReady(ReadyEvent e) {
    //updateAllUsers(client.getGuilds());
    //}

    @EventSubscriber
    public void onStatusChange(PresenceUpdateEvent e) {
        if (!statuses.containsKey(e.getUser().getLongID()) || e.getNewPresence().getStatus() != statuses.get(e.getUser().getLongID()).getStatus()) {
            updateUser(e.getUser());
        }
    }

    @EventSubscriber
    public void onJoinGuild(GuildCreateEvent e) {
        e.getGuild().getUsers().forEach(u -> updateUser(u));
    }

    @EventSubscriber
    public void onUserJoin(UserJoinEvent e) {
        updateUser(e.getUser());
    }

    private static String format(Timestamp time) {
        PrettyTime p = new PrettyTime();
        return p.format(time);
    }

    private void updateUser(IUser user) {
        if (statuses.containsKey(user.getLongID())) {
            UserStatus status = statuses.get(user.getLongID());
            if (status.getStatus() != user.getPresence().getStatus()) {
                status.updateStatus(user.getPresence().getStatus());
                StatusFileIO.saveStatus(status);
            }
        } else {
            UserStatus status = new UserStatus(user);
            statuses.put(user.getLongID(), status);
            StatusFileIO.saveStatus(status);
        }
    }
}
