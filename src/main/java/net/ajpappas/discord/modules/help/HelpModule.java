package net.ajpappas.discord.modules.help;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.CommandManager;
import net.ajpappas.discord.api.commands.CustomCommand;
import net.ajpappas.discord.utils.ChannelUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Created by Tony on 12/24/2016.
 */
public class HelpModule extends CustomModule implements IModule {

    public HelpModule() {
        super("Help Module", "1.1");
    }

    @BotCommand(command = "help", aliases = "h", module = "Help Module", description = "View all available commands.", usage = "Help [All]")
    public static void helpCommand(CommandContext cc) {
        String currentModule = "";
        boolean first = true;
        boolean all = false;
        if (cc.getArgCount() >= 2) {
            all = cc.getArgument(1).equalsIgnoreCase("all");
        }


        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();

        embed.withColor(36, 153, 153);
        embed.setLenient(true);

        for (CustomCommand command : CommandManager.getCommands()) {
            // Check if the user has permission for the command.
            if (!(all || cc.getUser().getPermissionsForGuild(cc.getGuild()).containsAll(command.getPermissions())))
                continue;

            if (!currentModule.equalsIgnoreCase(command.getModule())) {
                if (!currentModule.equals(""))
                    if (first) {
                        first = false;
                        embed.withTitle(currentModule);
                        embed.withDescription(sb.toString());
                    } else
                        embed.appendField(currentModule, sb.toString(), false);
                sb.setLength(0);
                currentModule = command.getModule();
            }

            if (command.isSecret())
                continue;
            if (command.getUsage(cc.getGuild()).length() > 1 && command.getDescription().length() > 0) {
                sb.append(command.getUsage(cc.getGuild())).append(" | ").append(command.getDescription()).append('\n');
            } else
                sb.append(command.getUsage(cc.getGuild()).length() > 1 ? command.getUsage(cc.getGuild()) : "!" + command.getName() + " " + command.getDescription()).append('\n');
        }

        embed.appendField(currentModule, sb.toString(), false);

        cc.replyWith(embed.build());
    }

    @EventSubscriber
    public void helpMention(MentionEvent e) {
        if (e.getMessage().getContent().toLowerCase().contains("prefix")) {
            ChannelUtil.sendMessage(e.getChannel(), "Prefix your commands with `" + CommandManager.getCommandPrefix(e.getGuild()) + "`");
        }
    }
}