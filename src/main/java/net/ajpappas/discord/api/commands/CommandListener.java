package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.Optional;

public class CommandListener {

    public CommandListener() {
        CommandModule.getClient().getDispatcher().registerListener(this);
    }

    @EventSubscriber
    public void onMesageEvent(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        IUser user = e.getAuthor();

        // Ignore bots
        if (user.isBot()) {
            return;
        }

        // Message is just a single prefix.
        if (message.length() <= 1) {
            return;
        }

        // Check if message started with our command prefix
        if (!message.startsWith(CommandManager.getCommandPrefix(e.getGuild()))) {
            return;
        }

        int userArgCount = message.split(" ").length;

        Optional<CustomCommand> customCommand = CommandManager.getCommands().stream().filter(command -> command.getCommands().length <= userArgCount).filter(command -> command.matches(message)).findFirst();

        long count = CommandManager.getCommands().stream().filter(command -> command.getCommands().length <= userArgCount && command.matches(message)).count();
        Logger.warning("Number of matching commands found: " + count);

        customCommand.ifPresent(command -> command.onMesageEvent(e));
    }
}
