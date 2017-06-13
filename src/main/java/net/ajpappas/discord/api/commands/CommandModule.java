package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.api.CustomModule;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 4/14/2017.
 */
public class CommandModule extends CustomModule implements IModule {

    public CommandModule() {
        super("Command Module", "1.0");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        CommandManager.initializeCommands();
    }
}
