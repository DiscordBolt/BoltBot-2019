package com.discordbolt.boltbot.modules.reddit.internal;

public class RawRedditObject {
    private String kind;
    private RawRedditPost data;

    public String getKind() {
        return kind;
    }

    public RawRedditPost getData() {
        return data;
    }
}
