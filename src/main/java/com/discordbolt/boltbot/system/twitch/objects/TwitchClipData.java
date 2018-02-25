package com.discordbolt.boltbot.system.twitch.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TwitchClipData {

    @SerializedName("data")
    public List<ClipData> data = null;

    public String getViewURL() {
        return "https://clips.twitch.tv/" + data.get(0).id;
    }

    private class ClipData {
        @SerializedName("id")
        public String id;

        @SerializedName("edit_url")
        public String editUrl;
    }
}
