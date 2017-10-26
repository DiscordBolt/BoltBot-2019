package net.ajpappas.discord.modules.reddit.internal;

public class RawRedditPost {
    public String id;
    public String url;
    public String subreddit;

    public String author; //null if promotional link
    public String title; //may contain newlines for some reason

    public String domain; //domain or if self post: "self.[subreddit]"
    public String post_hint; //image, link, sometimes null

    public int score;
    public int gilded;
    public boolean over_18;

    public String selftext;
}
