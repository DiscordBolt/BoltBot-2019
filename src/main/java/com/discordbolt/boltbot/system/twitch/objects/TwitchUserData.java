package com.discordbolt.boltbot.system.twitch.objects;

import com.google.gson.annotations.SerializedName;

public class TwitchUserData {

    @SerializedName("_id")
    private String id;

    @SerializedName("bio")
    private String bio;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("logo")
    private String logo;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}