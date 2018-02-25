package com.discordbolt.boltbot.system.twitch;


import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TwitchClip {

    private final TwitchAPI api;

    private final String CLIP_EDIT_URL = "https://api.twitch.tv/helix/clips";

    TwitchClip(TwitchAPI api) {
        this.api = api;
    }

    public String generateClip(String username) throws IOException {
        HttpUrl clipURL = HttpUrl.parse(CLIP_EDIT_URL).newBuilder().addQueryParameter("broadcaster_id", api.getUser().getUserID(username)).build();
        Request request = new Request.Builder().url(clipURL).addHeader("Authorization", api.getAuthHeader()).build();
        try (Response response = api.getClient().newCall(request).execute()) {
            return response.body().string();
        }
    }
}
