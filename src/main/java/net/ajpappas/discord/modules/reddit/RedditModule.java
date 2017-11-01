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

    //private static Pattern SUBREDDIT_PATTERN = Pattern.compile("(?:(?:https?:\\/\\/)?(?:www\\.)?(?:reddit\\.com)?\\/?r\\/)?([^\\s/]+)");
    private static Pattern SUBREDDIT_PATTERN = Pattern.compile("^(?:(?:https?:\\/\\/)?(?:www\\.)?reddit\\.com)?(?:\\/?r\\/)?(\\w+)$");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RedditModule(IDiscordClient client) {
        super(client, "Reddit Module", "0.1");
        registerDailyPosts();
    }

    @BotCommand(command = "reddit", module = "Reddit Module", description = "Get the What's Hot post", usage = "Reddit [subreddit]", args = 2)
    public static void redditCommand(CommandContext cc) {
        Matcher matcher = SUBREDDIT_PATTERN.matcher(cc.getArgument(1));

        if (matcher.find()) {
            try {
                cc.replyWith(Subreddit.getPosts(matcher.group(1), SortMethod.HOT).filter(false, PostType.IMAGE, PostType.SELF).getTopPost().toEmbed());
            } catch (IndexOutOfBoundsException e) {
                cc.replyWith("Sorry, no posts were able to be found.");
                return;
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

    private void registerDailyPosts() {
        scheduleTask("corgi", getClient().getChannelByID(Long.valueOf("110927581070512128")), 20, 0);
        scheduleTask("programmerhumor", getClient().getChannelByID(Long.valueOf("110927581070512128")), 12, 0);
    }

    private void scheduleTask(String subreddit, IChannel channel, int hour, int minute) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("America/New_York");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTime;
        zonedNextTime = zonedNow.withHour(hour).withMinute(minute).withSecond(0);
        if (zonedNow.compareTo(zonedNextTime) > 0)
            zonedNextTime = zonedNextTime.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNextTime);
        long initialDelay = duration.getSeconds();

        scheduler.scheduleAtFixedRate(new ScheduledSubreddit(subreddit, channel), initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
    }
}
