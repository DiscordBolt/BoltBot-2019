package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.modules.reddit.enums.PostType;
import net.ajpappas.discord.modules.reddit.enums.SortMethod;
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

        try {
            cc.replyWith(Subreddit.getPosts(subreddit, SortMethod.HOT).filter(PostType.IMAGE, PostType.SELF).getTopPost().toEmbed());
        } catch (IOException e) {
            cc.replyWith("IO Exception occurred, try again later.");
        } catch (IndexOutOfBoundsException e) {
            cc.replyWith("Sorry, no posts were able to be found.");
        }
    }
}
