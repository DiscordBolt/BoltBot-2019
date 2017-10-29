package net.ajpappas.discord.modules.help;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.CommandManager;
import net.ajpappas.discord.api.commands.CustomCommand;
import net.ajpappas.discord.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tony on 12/24/2016.
 */
public class HelpModule extends CustomModule implements IModule {

    public HelpModule(IDiscordClient client) {
        super(client, "Help Module", "1.1");
    }

    @BotCommand(command = "help", aliases = "h", module = "Help Module", description = "View all available commands.", usage = "Help [All]")
    public static void helpCommand(CommandContext cc) {

        String commandPrefix = CommandManager.getCommandPrefix(cc.getGuild());

        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(36, 153, 153);

        List<String> modules = CommandManager.getCommands().stream().map(CustomCommand::getModule).distinct().collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (String module : modules) {
            sb.setLength(0);

            for (CustomCommand command : CommandManager.getCommands().stream().filter(c -> c.getModule().equals(module)).collect(Collectors.toList())) {
                // Check if the user has permission for the command.
                if (!cc.getAuthor().getPermissionsForGuild(cc.getGuild()).containsAll(command.getPermissions()))
                    continue;
                if (command.isSecret())
                    continue;

                sb.append('`').append(commandPrefix).append(String.join(" ", command.getCommands())).append("` | ").append(command.getDescription()).append('\n');
            }

            embed.appendField(module, sb.toString(), false);
        }

        cc.replyWith(embed.build());
    }

    @EventSubscriber
    public void helpMention(MentionEvent e) {
        if (e.getMessage().getContent().toLowerCase().contains("prefix")) {
            ChannelUtil.sendMessage(e.getChannel(), "Prefix your commands with `" + CommandManager.getCommandPrefix(e.getGuild()) + "`");
        }
    }
}