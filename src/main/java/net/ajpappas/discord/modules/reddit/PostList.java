package net.ajpappas.discord.modules.reddit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PostList {

    private String subreddit;
    private SortMethod sortMethod;
    private Instant validUntil;
    private List<RedditPost> redditPostList;

    public PostList(String subreddit, SortMethod sortMethod, List<RedditPost> redditPostList) {
        this.subreddit = subreddit;
        this.sortMethod = sortMethod;
        this.validUntil = Instant.now().plus(Subreddit.CACHE_TTL, ChronoUnit.MINUTES);
        this.redditPostList = redditPostList;
    }

    public boolean isCacheValid() {
        return Instant.now().isBefore(validUntil);
    }
}
