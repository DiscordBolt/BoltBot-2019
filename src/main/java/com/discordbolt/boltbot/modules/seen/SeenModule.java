package com.discordbolt.boltbot.modules.seen;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.boltbot.api.CustomModule;
import com.discordbolt.boltbot.api.mysql.data.persistent.UserData;
import com.discordbolt.boltbot.utils.UserUtil;
import org.ocpsoft.prettytime.PrettyTime;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * Created by Tony on 4/3/2017.
 */
public class SeenModule extends CustomModule implements IModule {

    public SeenModule(IDiscordClient client) {
        super(client, "Seen Module", "1.2");
        client.getGuilds().forEach(g -> g.getUsers().forEach(this::updateUser));
    }

    @BotCommand(command = "seen", module = "Seen Module", description = "See when the user was last online.", usage = "Seen [User]", minArgs = 2, maxArgs = 100)
    public static void seenCommand(CommandContext cc) throws CommandException {
        IUser searchUser = UserUtil.findUser(cc.getMessage(), cc.getMessageContent().indexOf(' ') + 1);
        String name = cc.getMessageContent().substring(cc.getMessageContent().indexOf(' ') + 1, cc.getMessageContent().length());

        if (searchUser == null)
            throw new CommandArgumentException("Sorry, I could not find '" + name + "'.");

        Optional<UserData> userData = UserData.getById(searchUser.getLongID());
        if (!userData.isPresent() || userData.get().getLastStatusChange() == null)
            throw new CommandArgumentException("Sorry, I could not find \"" + name + "\".");

        cc.replyWith(searchUser.getName() + " has been " + userData.get().getStatus().name().replace("dnd", "do not disturb").toLowerCase() + " since " + format(userData.get().getLastStatusChange()) + '.');
    }

    @EventSubscriber
    public void onStatusChange(PresenceUpdateEvent e) {
        updateUser(e.getUser());
    }

    @EventSubscriber
    public void onJoinGuild(GuildCreateEvent e) {
        e.getGuild().getUsers().forEach(this::updateUser);
    }

    @EventSubscriber
    public void onUserJoin(UserJoinEvent e) {
        updateUser(e.getUser());
    }

    private static String format(Instant time) {
        return new PrettyTime().format(Timestamp.from(time));
    }

    private void updateUser(IUser user) {
        if (user.isBot())
            return;
        if (user.getPresence().getStatus() == UserData.getById(user.getLongID()).map(UserData::getStatus).orElse(null))
            return;
        UserData.getById(user.getLongID()).ifPresent(ud -> ud.setStatus(user.getPresence().getStatus()));
    }
}
