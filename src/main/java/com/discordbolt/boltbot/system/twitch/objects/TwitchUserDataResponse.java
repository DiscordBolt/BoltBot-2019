package com.discordbolt.boltbot.system.twitch.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class TwitchUserDataResponse {

    @SerializedName("_total")
    private int total;

    @SerializedName("users")
    private List<TwitchUserData> users = null;

    public int getTotal() {
        return total;
    }

    public List<TwitchUserData> getUsers() {
        return Collections.unmodifiableList(users);
    }
}