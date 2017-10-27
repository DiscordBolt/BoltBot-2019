package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.modules.reddit.enums.PostType;
import net.ajpappas.discord.modules.reddit.enums.SortMethod;
import net.ajpappas.discord.modules.reddit.posts.RedditPost;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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

    public String getSubreddit() {
        return subreddit;
    }

    public SortMethod getSortMethod() {
        return sortMethod;
    }

    public boolean isCacheValid() {
        return Instant.now().isBefore(validUntil);
    }

    public RedditPost get(int index) {
        return redditPostList.get(index);
    }

    public List<RedditPost> get() {
        return redditPostList;
    }

    public RedditPost getTopPost() {
        if (get().size() <= 0)
            throw new IndexOutOfBoundsException("The post list is empty!");
        return get(0);
    }

    public PostList filter(PostType... postTypes) {
        return filter(true, postTypes);
    }

    public PostList filter(boolean allowNSFW, PostType... postTypes) {
        return new PostList(getSubreddit(), getSortMethod(), get().stream().filter(s -> !s.isNSFW() || allowNSFW).filter(s -> s.isPostType(postTypes)).collect(Collectors.toList()));
    }
}
