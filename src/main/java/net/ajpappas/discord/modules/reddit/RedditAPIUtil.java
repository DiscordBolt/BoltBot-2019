package net.ajpappas.discord.modules.reddit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ajpappas.discord.modules.reddit.data.Subreddit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class RedditAPIUtil {

    private static OkHttpClient client = new OkHttpClient();
    private static final String REDDIT_URL = "https://www.reddit.com/r/{subreddit}/top.json?t=day";
    private static JsonParser parser = new JsonParser();

    /**
     * Get the top post of the past 24 hours from the given subreddit
     *
     * @param subreddit subreddit name, no /r/ formatting
     * @return Subreddit that contains the top post
     */
    public static Subreddit getTopPost(String subreddit) throws IOException {
        Request request = new Request.Builder().url(REDDIT_URL.replace("{subreddit}", subreddit)).build();

        Response response = client.newCall(request).execute();

        JsonObject json = parser.parse(response.body().string()).getAsJsonObject();

        RedditPost post = new RedditPost();
        post.setUrl(json.getAsJsonObject());
    }
}
