package Techtony96.Discord.modules.help;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.CustomCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.CommandManager;
import Techtony96.Discord.api.commands.BotCommand;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 12/24/2016.
 */
public class HelpModule extends CustomModule implements IModule {

    public HelpModule() {
        super("Help Command", "1.1");
    }

    @BotCommand(command = "help", aliases = "h", description = "View all commands.", usage = "!Help")
    public static void helpCommand(CommandContext cc) {
        StringBuilder sb = new StringBuilder();
        for (CustomCommand bc : CommandManager.getCommands()) {
            if (bc.isSecret())
                continue;
            if (bc.getUsage().length() > 0 && bc.getDescription().length() > 0) {
                sb.append(bc.getUsage()).append(" | ").append(bc.getDescription()).append('\n');
            } else
                sb.append(bc.getUsage().length() > 0 ? bc.getUsage() : "!" + bc.getName() + " " + bc.getDescription()).append('\n');
        }
        cc.replyWith(sb.toString());
    }
}