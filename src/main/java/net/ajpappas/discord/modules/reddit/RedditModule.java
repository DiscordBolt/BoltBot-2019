package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

public class RedditModule extends CustomModule implements IModule {

    public RedditModule(IDiscordClient client) {
        super(client, "Reddit Module", "0.1");
    }

    @BotCommand(command = "reddit", module = "Reddit Module", description = "Post the top post of the last 24 hours from the given subreddit", usage = "Reddit /r/subreddit")
    public static void redditCommand(CommandContext cc) {

    }
}
