package net.ajpappas.discord.modules.reddit;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.modules.reddit.enums.PostType;
import net.ajpappas.discord.modules.reddit.enums.SortMethod;
import net.ajpappas.discord.utils.ExceptionMessage;
import net.ajpappas.discord.utils.Logger;
import org.apache.http.client.HttpResponseException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.modules.IModule;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedditModule extends CustomModule implements IModule {

    private static Pattern SUBREDDIT_PATTERN = Pattern.compile("^(?:(?:https?:\\/\\/)?(?:www\\.)?reddit\\.com)?(?:\\/?r\\/)?(\\w+)$");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RedditModule(IDiscordClient client) {
        super(client, "Reddit Module", "0.1");
    }

    @BotCommand(command = "reddit", module = "Reddit Module", description = "Get the What's Hot post", usage = "Reddit [subreddit]", args = 2)
    public static void redditCommand(CommandContext cc) {
        Matcher matcher = SUBREDDIT_PATTERN.matcher(cc.getArgument(1));

        if (matcher.find()) {
            try {
                cc.replyWith(Subreddit.getPosts(matcher.group(1), SortMethod.HOT).filter(false, PostType.IMAGE, PostType.SELF).getTopPost().toEmbed());
            } catch (IndexOutOfBoundsException e) {
                cc.replyWith("Sorry, no posts were able to be found.");
            } catch (HttpResponseException e) {
                cc.replyWith("That subreddit doesn't exist!");
            } catch (IOException e) {
                Logger.error(e.getMessage());
                Logger.debug(e);
                cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
            }
        } else {
            cc.sendUsage();
        }
    }

    @BotCommand(command = {"reddit", "subscribe"}, description = "Subscribe to get daily top posts in your DMs.", usage = "Reddit Subscribe [subreddit] [time]", module = "Reddit Module")
    public static void redditSubscribeCommand(CommandContext cc) {

    }

    private void scheduleTask(String subreddit, IChannel channel, int hour, int minute) {
        ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/New_York"));
        ZonedDateTime zonedNextTime;
        zonedNextTime = zonedNow.withHour(hour).withMinute(minute).withSecond(0);
        if (zonedNow.compareTo(zonedNextTime) > 0)
            zonedNextTime = zonedNextTime.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNextTime);
        long initialDelay = duration.getSeconds();

        scheduler.scheduleAtFixedRate(new ScheduledSubreddit(subreddit, channel), initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
    }
}
