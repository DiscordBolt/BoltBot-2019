package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

import java.io.IOException;
import java.util.Optional;

public class RedditModule extends CustomModule implements IModule {

    public RedditModule(IDiscordClient client) {
        super(client, "Reddit Module", "0.1");
    }

    @BotCommand(command = "reddit", module = "Reddit Module", description = "Post the top post of the last 24 hours from the given subreddit", usage = "Reddit /r/subreddit")
    public static void redditCommand(CommandContext cc) {
        String subreddit = cc.getArgument(1);

        try {
            Optional<RedditPost> post = Subreddit.getHotImage(subreddit);
            if (post.isPresent()) {
                cc.replyWith("Here is your post!");
                cc.replyWith(post.get().toEmbed());
            } else {
                cc.replyWith("No images where found in the hot listing of that subreddit.");
            }
        } catch (IOException e) {
            cc.replyWith("IO Exception occurred, try again later.");
        }
    }
}
