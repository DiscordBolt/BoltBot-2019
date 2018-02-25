package com.discordbolt.boltbot.system.twitch;

import com.discordbolt.boltbot.system.twitch.objects.TwitchUserData;
import com.discordbolt.boltbot.system.twitch.objects.TwitchUserDataResponse;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

public class TwitchUser {

    private final TwitchAPI api;

    private final String USER_INFO_URL = "https://api.twitch.tv/kraken/users";

    TwitchUser(TwitchAPI api) {
        this.api = api;
    }

    public Optional<TwitchUserData> getUserData(String username) throws IOException {
        HttpUrl userURL = HttpUrl.parse(USER_INFO_URL).newBuilder().addQueryParameter("login", username).build();
        Request request = new Request.Builder().url(userURL).addHeader("Client-ID", api.getClientID()).addHeader("Accept", "application/vnd.twitchtv.v5+json").build();
        try (Response response = api.getClient().newCall(request).execute()) {
            TwitchUserDataResponse users = api.getGson().fromJson(response.body().string(), TwitchUserDataResponse.class);
            return users.getUsers().stream().findFirst();
        }
    }

    public String getUserId(String username) throws IOException {
        Optional<TwitchUserData> user = getUserData(username);
        if (user.isPresent()) {
            return user.get().getId();
        } else
            return null;
    }
}
