package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

import java.io.IOException;

public class RedditModule extends CustomModule implements IModule {

    public RedditModule(IDiscordClient client) {
        super(client, "Reddit Module", "0.1");
    }

    @BotCommand(command = "reddit", module = "Reddit Module", description = "Post the top post of the last 24 hours from the given subreddit", usage = "Reddit /r/subreddit")
    public static void redditCommand(CommandContext cc) {
        String subreddit = cc.getArgument(1);
        cc.replyWith("Getting " + subreddit + " top post");
        Subreddit reddit;
        try {
            reddit = new Subreddit(subreddit);
            Logger.info("Created reddit object");
        } catch (IOException e) {
            cc.replyWith("Could not get posts");
            return;
        }
        if (reddit.getTopPost().getUrl() != null)
            cc.replyWith("url: " + reddit.getTopPost().getUrl());
        else
            cc.replyWith("no images");
    }
}
