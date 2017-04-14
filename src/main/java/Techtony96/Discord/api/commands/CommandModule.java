package Techtony96.Discord.api.commands;

import Techtony96.Discord.api.CustomModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 4/14/2017.
 */
public class CommandModule extends CustomModule implements IModule {

    public static IDiscordClient client;

    public CommandModule() {
        super("Command Module", "1.0");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        client = getClient();
        CommandManager.initializeCommands();
    }
}
