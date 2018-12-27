package com.discordbolt.boltbot.discord.modules.twitch.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelData {

    @JsonProperty("data")
    private List<Channel> data = new ArrayList<>();

    public boolean dataIsEmpty() {
        return data.isEmpty();
    }

    public long getId() {
        return data.isEmpty() ? 0 : data.get(0).id;
    }

    public long getUserId() {
        return data.isEmpty() ? 0 : data.get(0).userId;
    }

    public String getUsername() {
        return data.isEmpty() ? null : data.get(0).username;
    }

    public long getGameId() {
        return data.isEmpty() ? 0 : data.get(0).gameId;
    }

    public List<String> getCommunityIds() {
        return data.isEmpty() ? Collections.emptyList() : data.get(0).communityIds;
    }

    public String getType() {
        return data.isEmpty() ? null : data.get(0).type;
    }

    public String getTitle() {
        return data.isEmpty() ? null : data.get(0).title;
    }

    public int getViewerCount() {
        return data.isEmpty() ? 0 : data.get(0).viewerCount;
    }

    public Instant getStartedAt() {
        return data.isEmpty() ? null : data.get(0).startedAt;
    }

    public String getLanguage() {
        return data.isEmpty() ? null : data.get(0).language;
    }

    public String getThumbnailUrl() {
        return data.isEmpty() ? null : data.get(0).thumbnailUrl;
    }

    public List<String> getTagIds() {
        return data.isEmpty() ? Collections.emptyList() : data.get(0).tagIds;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class Channel {

    @JsonProperty("id")
    long id;
    @JsonProperty("user_id")
    long userId;
    @JsonProperty("user_name")
    String username;
    @JsonProperty("game_id")
    long gameId;
    @JsonProperty("community_ids")
    List<String> communityIds = null;
    @JsonProperty("type")
    String type;
    @JsonProperty("title")
    String title;
    @JsonProperty("viewer_count")
    int viewerCount;
    @JsonProperty("started_at")
    Instant startedAt;
    @JsonProperty("language")
    String language;
    @JsonProperty("thumbnail_url")
    String thumbnailUrl;
    @JsonProperty("tag_ids")
    List<String> tagIds = null;
}
