package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.api.CustomModule;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 4/14/2017.
 */
public class CommandModule extends CustomModule implements IModule {

    public CommandModule() {
        super("Command Module", "1.0");
        CommandManager.initializeCommands();
    }

}
