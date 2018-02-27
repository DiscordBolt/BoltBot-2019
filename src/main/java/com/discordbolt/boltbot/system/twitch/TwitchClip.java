package com.discordbolt.boltbot.system.twitch;


import com.discordbolt.boltbot.system.twitch.objects.TwitchClipData;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class TwitchClip {

    private final TwitchAPI api;

    private final String CLIP_EDIT_URL = "https://api.twitch.tv/helix/clips";

    TwitchClip(TwitchAPI api) {
        this.api = api;
    }

    public String generateClip(String username) throws IOException {
        String userID = api.getUser().getUserId(username);
        if (userID == null)
            throw new IllegalArgumentException(username + " does not exist!");
        HttpUrl clipURL = HttpUrl.parse(CLIP_EDIT_URL).newBuilder().addQueryParameter("broadcaster_id", userID).build();
        Request request = new Request.Builder().post(RequestBody.create(null, new byte[0])).url(clipURL).addHeader("Authorization", api.getAuthHeader()).build();
        try (Response response = api.getClient().newCall(request).execute()) {
            if (response.code() == TwitchAPI.UNAUTHORIZED_RESPONSE_CODE) {
                try {
                    api.refreshOAuthToken();
                    Response newResponse = api.getClient().newCall(request).execute();
                    return api.getGson().fromJson(newResponse.body().string(), TwitchClipData.class).getViewURL();
                } catch (Exception e) {
                    return null;
                }
            }
            return api.getGson().fromJson(response.body().string(), TwitchClipData.class).getViewURL();
        }
    }
}
