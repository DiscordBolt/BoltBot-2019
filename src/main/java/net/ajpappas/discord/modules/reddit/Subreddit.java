package net.ajpappas.discord.modules.reddit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ajpappas.discord.utils.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Subreddit {

    private String subredditName;
    private List<RedditObject> posts;

    private static OkHttpClient client = new OkHttpClient();
    private static final String REDDIT_URL = "https://www.reddit.com/r/{subreddit}/top.json?t=day";
    private static JsonParser parser = new JsonParser();
    private static Gson gson = new Gson();

    public Subreddit(String subredditName) throws IOException {
        this.subredditName = subredditName;

        try {
            posts = getTopPosts(subredditName);
        } catch (IOException ex) {
            Logger.error("Failed to get posts of subreddit \"" + subredditName + "\"");
            Logger.debug(ex);
            throw ex;
        }
        Logger.info("Subreddit constructor finished");
    }

    public RedditPost getTopPost() {
        Logger.info("Looking for top post");
        for (RedditObject post : posts) {
            if (post.data.getPost_hint().equals("image")) {
                Logger.info("Found an image post!");
                return post.data;
            }
        }
        Logger.warning("No post found for subreddit: " + subredditName);
        return null;
    }

    /**
     * Get the top post of the past 24 hours from the given subreddit
     *
     * @param subreddit subreddit name, no /r/ formatting
     * @return Subreddit that contains the top post
     */
    private static List<RedditObject> getTopPosts(String subreddit) throws IOException {
        Request request = new Request.Builder().url(REDDIT_URL.replace("{subreddit}", subreddit)).build();

        Logger.info("Sending request");
        Response response = client.newCall(request).execute();
        Logger.info("Request done");

        if (!response.isSuccessful())
            throw new HttpResponseException(response.code(), response.message());

        Logger.info("Request successful");

        JsonObject json = parser.parse(response.body().string()).getAsJsonObject();

        Logger.info("Json conversion complete");

        return Arrays.asList(gson.fromJson(json.getAsJsonObject("data").getAsJsonArray("children"), RedditObject[].class));
    }

    private class RedditObject {
        private String kind;
        private RedditPost data;
    }

    class RedditPost {

        private String id;
        private String url;

        private String author; //null if promotional link
        private String title; //may contain newlines for some reason

        private String domain; //domain or if self post: "self.[subreddit]"
        private String post_hint; //image, link

        private int score;
        private int gilded;
        private boolean over_18;

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public String getAuthor() {
            return author;
        }

        public String getTitle() {
            return title;
        }

        public String getDomain() {
            return domain;
        }

        public String getPost_hint() {
            return post_hint;
        }

        public int getScore() {
            return score;
        }

        public int getGilded() {
            return gilded;
        }

        public boolean isOver_18() {
            return over_18;
        }
    }
}
