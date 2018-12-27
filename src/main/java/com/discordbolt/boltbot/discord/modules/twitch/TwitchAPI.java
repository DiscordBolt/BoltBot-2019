package com.discordbolt.boltbot.discord.modules.twitch;


import com.discordbolt.boltbot.discord.modules.twitch.responses.ChannelData;
import com.discordbolt.boltbot.discord.modules.twitch.responses.UserData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component("TwitchAPI")
@Profile("prod")
public class TwitchAPI {

    private String clientId;

    public TwitchAPI(@Value("${twitch.clientid}") String clientId) {
        this.clientId = clientId;
    }

    public ResponseEntity<ChannelData> getChannelData(String channelName) {
        return new RestTemplate().exchange("https://api.twitch.tv/helix/streams?user_login=" + channelName, HttpMethod.GET, getHeaders(), ChannelData.class);
    }

    public ResponseEntity<UserData> getUserData(String channelName) {
        return new RestTemplate().exchange("https://api.twitch.tv/helix/users?login=" + channelName, HttpMethod.GET, getHeaders(), UserData.class);

    }

    private HttpEntity getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-ID", clientId);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("", headers);
    }
}
