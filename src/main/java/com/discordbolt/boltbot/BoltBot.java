package com.discordbolt.boltbot;

import com.discordbolt.api.command.CommandManager;
import com.discordbolt.boltbot.modules.dice.DiceModule;
import com.discordbolt.boltbot.modules.disconnect.DisconnectModule;
import com.discordbolt.boltbot.modules.misc.CuntModule;
import com.discordbolt.boltbot.modules.misc.TableFixerModule;
import com.discordbolt.boltbot.modules.music.MusicModule;
import com.discordbolt.boltbot.modules.reddit.RedditModule;
import com.discordbolt.boltbot.modules.seen.SeenModule;
import com.discordbolt.boltbot.modules.streamannouncer.StreamAnnouncer;
import com.discordbolt.boltbot.modules.tags.TagModule;
import com.discordbolt.boltbot.system.mysql.MySQL;
import com.discordbolt.boltbot.system.mysql.data.DataSync;
import com.discordbolt.boltbot.system.mysql.data.persistent.GuildData;
import com.discordbolt.boltbot.system.status.StatusModule;
import com.discordbolt.boltbot.utils.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.sql.SQLException;

/**
 * Created by Tony on 6/13/2017.
 */
public class BoltBot {

    private static CommandManager commandManager;

    public static void main(String[] args) {
        if (args.length < 1) {
            Logger.error("No Bot Token specified.");
            return;
        }

        try {
            MySQL.getDataSource().getConnection();
        } catch (SQLException e) {
            Logger.error("Can not connect to MySQL database. Configure database.properties");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {

            }
            System.exit(1);
        }

        IDiscordClient client = new ClientBuilder().withToken(args[0]).login();

        client.getDispatcher().registerListener((IListener<ReadyEvent>) (ReadyEvent e) -> {
            Logger.info("Logged in as " + e.getClient().getOurUser().getName());
            registerModules(client);
        });
    }

    private static void registerModules(IDiscordClient client) {
        // API Modules
        Logger.trace("Loading API modules.");
        client.getDispatcher().registerListener(new DataSync(client));

        commandManager = new CommandManager(client, "com.discordbolt.boltbot");
        client.getGuilds().stream().map(g -> GuildData.getById(g.getLongID())).filter(gd -> gd.isPresent()).filter(gd -> gd.get().getCommandPrefix() != null).forEach(gd -> commandManager.setCommandPrefix(client.getGuildByID(gd.get().getGuildId()), gd.get().getCommandPrefix().charAt(0)));
        Logger.trace("Finished loading API modules");

        // Feature Modules
        Logger.trace("Loading feature modules.");
        client.getModuleLoader().loadModule(new MusicModule(client));
        client.getModuleLoader().loadModule(new CuntModule(client));
        client.getModuleLoader().loadModule(new DiceModule(client));
        client.getModuleLoader().loadModule(new DisconnectModule(client));
        client.getModuleLoader().loadModule(new RedditModule(client));
        client.getModuleLoader().loadModule(new TableFixerModule(client));
        client.getModuleLoader().loadModule(new SeenModule(client));
        client.getModuleLoader().loadModule(new StatusModule(client));
        client.getModuleLoader().loadModule(new StreamAnnouncer(client));
        client.getModuleLoader().loadModule(new TagModule(client));
        Logger.trace("Finished loading feature modules.");
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }
}
