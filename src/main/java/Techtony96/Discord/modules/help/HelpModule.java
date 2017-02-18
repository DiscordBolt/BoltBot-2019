package Techtony96.Discord.modules.help;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.CommandManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 12/24/2016.
 */
public class HelpModule extends CustomModule implements IModule {


    public HelpModule() {
       super("Help Command", "1.1");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e){
        new BotCommand(client, "help"){
            @Override
            public void execute(CommandContext cc) {
                StringBuilder sb = new StringBuilder();
                for (BotCommand bc : CommandManager.getCommands()){
                    if (bc.isSecret())
                        continue;
                    if (bc.getUsage().length() > 0 && bc.getDescription().length() > 0){
                        sb.append(bc.getUsage() + " | " + bc.getDescription() + '\n');
                    } else
                        sb.append((bc.getUsage().length() > 0 ? bc.getUsage() : "!" + bc.getName() + " " + bc.getDescription()) + '\n');
                }
                cc.replyWith(sb.toString());
            }
        }.setAliases("h").setSecret(true);
    }
}
