package com.discordbolt.boltbot.system.twitch.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class TwitchAccessToken {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("scope")
    private List<String> scopes;

    @SerializedName("expires_in")
    private int expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public List<String> getScopes() {
        return Collections.unmodifiableList(scopes);
    }

    /**
     * The number of seconds until the token expires
     *
     * @return
     */
    public int getExpiresIn() {
        return expiresIn;
    }
}
