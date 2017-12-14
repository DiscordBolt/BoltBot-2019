package net.ajpappas.discord;

import com.discordbolt.api.command.CommandManager;
import net.ajpappas.discord.api.mysql.MySQL;
import net.ajpappas.discord.api.mysql.data.DataSync;
import net.ajpappas.discord.api.mysql.data.persistent.GuildData;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.modules.dice.DiceModule;
import net.ajpappas.discord.modules.disconnect.DisconnectModule;
import net.ajpappas.discord.modules.log.LogModule;
import net.ajpappas.discord.modules.misc.CuntModule;
import net.ajpappas.discord.modules.misc.TableFixerModule;
import net.ajpappas.discord.modules.reddit.RedditModule;
import net.ajpappas.discord.modules.seen.SeenModule;
import net.ajpappas.discord.modules.status.StatusModule;
import net.ajpappas.discord.modules.streamannouncer.StreamAnnouncer;
import net.ajpappas.discord.modules.tags.TagModule;
import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.Configuration;

import java.sql.SQLException;

/**
 * Created by Tony on 6/13/2017.
 */
public class Discord {

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

        Configuration.AUTOMATICALLY_ENABLE_MODULES = false;
        Configuration.LOAD_EXTERNAL_MODULES = false;

        ClientBuilder builder = new ClientBuilder();
        IDiscordClient client = builder.withToken(args[0]).login();

        client.getDispatcher().registerListener((IListener<ReadyEvent>) (ReadyEvent e) -> {
            Logger.info("Logged in as " + e.getClient().getOurUser().getName());
            registerModules(client);
        });
    }

    private static void registerModules(IDiscordClient client) {
        // API Modules
        Logger.trace("Loading API modules.");
        client.getDispatcher().registerListener(new DataSync(client));

        commandManager = new CommandManager(client, "net.ajpappas.discord");
        client.getGuilds().stream().map(g -> GuildData.getById(g.getLongID())).filter(gd -> gd.isPresent()).filter(gd -> gd.get().getCommandPrefix() != null).forEach(gd -> commandManager.setCommandPrefix(client.getGuildByID(gd.get().getGuildId()), gd.get().getCommandPrefix().charAt(0)));

        client.getModuleLoader().loadModule(new LogModule(client));
        Logger.trace("Finished loading API modules");

        // Feature Modules
        Logger.trace("Loading feature modules.");
        client.getModuleLoader().loadModule(new AudioStreamer(client));
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
