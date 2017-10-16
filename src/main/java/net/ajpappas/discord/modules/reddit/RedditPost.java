package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.modules.reddit.internal.RawRedditPost;
import net.ajpappas.discord.utils.EmbedUtil;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

public class RedditPost {

    //private static final Color EMBED_COLOR = new Color(0, 153, 204);
    private static final Color EMBED_COLOR = new Color(255, 165, 0);

    private String id;
    private String url;
    private String subreddit;

    private String author;
    private String title;

    private String domain;
    private PostType postHint;

    private int score;
    private int gilded;
    private boolean over_18;

    public RedditPost(RawRedditPost rawRedditPost) {
        this.id = rawRedditPost.id;
        this.url = rawRedditPost.url;
        this.subreddit = rawRedditPost.subreddit;
        this.author = rawRedditPost.author;
        this.title = rawRedditPost.title;
        this.domain = rawRedditPost.domain;
        this.postHint = PostType.getEnum(rawRedditPost.post_hint);
        this.score = rawRedditPost.score;
        this.gilded = rawRedditPost.gilded;
        this.over_18 = rawRedditPost.over_18;
    }

    public boolean isSelfPost() {
        return domain.equals("self." + subreddit);
    }

    public boolean isImage() {
        return domain.equals("i.redd.it");
    }

    public boolean isVideo() {
        return domain.equals("v.redd.it");
    }

    public EmbedObject toEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(EMBED_COLOR);
        embed.withAuthorName(getTitle());
        embed.withAuthorUrl("https://www.reddit.com/r/" + getSubreddit() + "/comments/" + getId());
        embed.withDesc("Posted by: " + getAuthor());
        embed.withImage(getUrl());
        embed.appendField(":arrow_up: " + getScore(), EmbedUtil.ZERO_WIDTH_SPACE, true);
        embed.appendField(":star: x" + getGilded(), EmbedUtil.ZERO_WIDTH_SPACE, true);
        return embed.build();
    }

    /* Getters and Setters */

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDomain() {
        return domain;
    }

    public PostType getPostHint() {
        return postHint;
    }

    public int getScore() {
        return score;
    }

    public int getGilded() {
        return gilded;
    }

    public boolean isOver_18() {
        return over_18;
    }
}
