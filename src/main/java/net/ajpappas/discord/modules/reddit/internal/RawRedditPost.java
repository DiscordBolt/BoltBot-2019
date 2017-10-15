package net.ajpappas.discord.modules.reddit.internal;

public class RawRedditPost {
    private String id;
    private String url;

    private String author; //null if promotional link
    private String title; //may contain newlines for some reason

    private String domain; //domain or if self post: "self.[subreddit]"
    private String post_hint; //image, link

    private int score;
    private int gilded;
    private boolean over_18;
}
