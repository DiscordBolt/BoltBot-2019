package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.modules.reddit.enums.PostType;
import net.ajpappas.discord.modules.reddit.enums.SortMethod;
import net.ajpappas.discord.utils.ExceptionMessage;
import net.ajpappas.discord.utils.Logger;
import org.apache.http.client.HttpResponseException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedditModule extends CustomModule implements IModule {

    //private static Pattern SUBREDDIT_PATTERN = Pattern.compile("(?:(?:https?:\\/\\/)?(?:www\\.)?(?:reddit\\.com)?\\/?r\\/)?([^\\s/]+)");
    private static Pattern SUBREDDIT_PATTERN = Pattern.compile("^(?:(?:https?:\\/\\/)?(?:www\\.)?reddit\\.com)?(?:\\/?r\\/)?(\\w+)$");

    public RedditModule(IDiscordClient client) {
        super(client, "Reddit Module", "0.1");
    }

    @BotCommand(command = "reddit", module = "Reddit Module", description = "Post the top post of the last 24 hours from the given subreddit", usage = "Reddit /r/subreddit")
    public static void redditCommand(CommandContext cc) {
        Matcher matcher = SUBREDDIT_PATTERN.matcher(cc.getArgument(1));

        if (matcher.find()) {
            try {
                cc.replyWith(Subreddit.getPosts(matcher.group(1), SortMethod.HOT).filter(false, PostType.IMAGE, PostType.SELF).getTopPost().toEmbed());
            } catch (IndexOutOfBoundsException e) {
                cc.replyWith("Sorry, no posts were able to be found.");
            } catch (HttpResponseException e) {
                cc.replyWith("That subreddit doesn't exist!");
                return;
            } catch (IOException e) {
                Logger.error(e.getMessage());
                Logger.debug(e);
                cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
                return;
            }
        } else {
            cc.sendUsage();
            return;
        }
    }
}
