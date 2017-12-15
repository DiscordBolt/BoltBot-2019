package com.discordbolt.boltbot.modules.reddit.posts;

import com.discordbolt.boltbot.modules.reddit.enums.PostType;
import com.discordbolt.boltbot.modules.reddit.internal.RawRedditPost;
import sx.blah.discord.api.internal.json.objects.EmbedObject;

import java.awt.*;
import java.util.Arrays;

public abstract class RedditPost {

    //private static final Color EMBED_COLOR = new Color(0, 153, 204);
    static final Color EMBED_COLOR = new Color(255, 165, 0);

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
        this.postHint = convertPostType(rawRedditPost);
        this.score = rawRedditPost.score;
        this.gilded = rawRedditPost.gilded;
        this.over_18 = rawRedditPost.over_18;
    }

    private PostType convertPostType(RawRedditPost rawRedditPost) {
        // Attempt using reddit's post_hint field first
        PostType postType = PostType.getEnum(rawRedditPost.post_hint);
        if (postType != null)
            return postType;

        postType = PostType.convertDomain(rawRedditPost.domain);
        if (postType != null)
            return postType;

        postType = PostType.convertURL(rawRedditPost.url);
        if (postType != null)
            return postType;

        return PostType.UNKNOWN;
    }

    public abstract EmbedObject toEmbed();

    public boolean isSelfPost() {
        return domain.equals("self." + subreddit);
    }

    public boolean isImage() {
        return domain.equals("i.redd.it");
    }

    public boolean isVideo() {
        return domain.equals("v.redd.it");
    }

    public boolean isNSFW() {
        return over_18;
    }

    public boolean isPostType(PostType... postTypes) {
        return Arrays.asList(postTypes).contains(getPostType());
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

    public PostType getPostType() {
        return postHint;
    }

    public int getScore() {
        return score;
    }

    public int getGilded() {
        return gilded;
    }
}
