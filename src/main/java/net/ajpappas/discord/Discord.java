package net.ajpappas.discord;

import net.ajpappas.discord.api.commands.CommandModule;
import net.ajpappas.discord.modules.audiostreamer.AudioStreamer;
import net.ajpappas.discord.modules.dice.DiceModule;
import net.ajpappas.discord.modules.disconnect.DisconnectModule;
import net.ajpappas.discord.modules.help.HelpModule;
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

/**
 * Created by Tony on 6/13/2017.
 */
public class Discord {

    public static void main(String[] args) {
        if (args.length < 1) {
            Logger.error("No Bot Token specified.");
            return;
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
        client.getModuleLoader().loadModule(new CommandModule(client));
        client.getModuleLoader().loadModule(new LogModule(client));

        Logger.debug("API MODULES LOADED");

        // Feature Modules
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

        // Dependent Modules
        client.getModuleLoader().loadModule(new HelpModule(client));
    }
}
