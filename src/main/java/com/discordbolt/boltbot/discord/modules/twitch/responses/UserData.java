package com.discordbolt.boltbot.discord.modules.twitch.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

    @JsonProperty("data")
    private List<User> data = new ArrayList<>();

    public boolean dataIsEmpty() {
        return data.isEmpty();
    }

    public long getId() {
        return data.isEmpty() ? 0 : data.get(0).id;
    }

    public String getLogin() {
        return data.isEmpty() ? null : data.get(0).login;
    }

    public String getDisplayName() {
        return data.isEmpty() ? null : data.get(0).displayName;
    }

    public String getType() {
        return data.isEmpty() ? null : data.get(0).type;
    }

    public String getBroadcasterType() {
        return data.isEmpty() ? null : data.get(0).broadcasterType;
    }

    public String getDescription() {
        return data.isEmpty() ? null : data.get(0).description;
    }

    public String getProfileImageUrl() {
        return data.isEmpty() ? null : data.get(0).profileImageUrl;
    }

    public String getOfflineImageUrl() {
        return data.isEmpty() ? null : data.get(0).offlineImageUrl;
    }

    public int getViewCount() {
        return data.isEmpty() ? 0 : data.get(0).viewCount;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class User {
    @JsonProperty("id")
    long id;
    @JsonProperty("login")
    String login;
    @JsonProperty("display_name")
    String displayName;
    @JsonProperty("type")
    String type;
    @JsonProperty("broadcaster_type")
    String broadcasterType;
    @JsonProperty("description")
    String description;
    @JsonProperty("profile_image_url")
    String profileImageUrl;
    @JsonProperty("offline_image_url")
    String offlineImageUrl;
    @JsonProperty("view_count")
    int viewCount;
}