package com.discordbolt.boltbot.modules.reddit;

import com.discordbolt.boltbot.modules.reddit.enums.SortMethod;
import com.discordbolt.boltbot.modules.reddit.internal.RawRedditObject;
import com.discordbolt.boltbot.modules.reddit.posts.ImagePost;
import com.discordbolt.boltbot.modules.reddit.posts.RedditPost;
import com.discordbolt.boltbot.modules.reddit.posts.SelfPost;
import com.discordbolt.boltbot.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subreddit {

    private static Map<String, Subreddit> subreddits = new HashMap<>();
    private static OkHttpClient client = new OkHttpClient();
    private static JsonParser parser = new JsonParser();
    private static Gson gson = new Gson();
    public static final int CACHE_TTL = 30; // Number of minutes we won't send a new request to reddit for

    private String subredditName;
    private HashMap<SortMethod, PostList> posts = new HashMap<>();

    private Subreddit(String subredditName) {
        this.subredditName = subredditName.toLowerCase();
    }

    /**
     * Get the first 25 posts of a given subreddit with given sorting method
     *
     * @param subredditName Subreddit string to fetch posts of. No formatting.
     * @param sortMethod    How you want reddit to sort the returned posts
     * @return List of raw reddit objects
     * @throws IOException If response fails or other IOException occurs
     */
    public static PostList getPosts(String subredditName, SortMethod sortMethod) throws IOException {
        // Check the cache first
        if (subreddits.containsKey(subredditName.toLowerCase())) {
            // Subreddit exists in cache
            Subreddit subreddit = subreddits.get(subredditName.toLowerCase());
            if (subreddit.posts.containsKey(sortMethod)) {
                // Sort method exists in subreddit
                if (subreddit.posts.get(sortMethod).isCacheValid()) {
                    // Cache hit
                    return subreddit.posts.get(sortMethod);
                }
            }
        } else if (!subredditName.equalsIgnoreCase("random")) {
            subreddits.put(subredditName.toLowerCase(), new Subreddit(subredditName));
        }

        Logger.debug("Cache miss for subreddit " + subredditName + " with sorting method " + sortMethod.name());

        // Cache miss, request the info from Github
        Request request = new Request.Builder().url("https://api.reddit.com/r/" + subredditName + sortMethod).build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful())
            throw new HttpResponseException(response.code(), response.message());

        JsonObject json = parser.parse(response.body().string()).getAsJsonObject();
        RawRedditObject[] rawRedditObjects = gson.fromJson(json.getAsJsonObject("data").getAsJsonArray("children"), RawRedditObject[].class);

        List<RedditPost> redditPosts = new ArrayList<>();

        for (RawRedditObject raw : rawRedditObjects) {
            if (raw.getData().stickied.equalsIgnoreCase("true"))
                continue;
            if (raw.getData().domain.equalsIgnoreCase("self." + raw.getData().subreddit)) {
                redditPosts.add(new SelfPost(raw.getData()));
            } else {
                redditPosts.add(new ImagePost(raw.getData()));
            }
        }

        response.close();

        PostList ps = new PostList(subredditName, sortMethod, redditPosts);
        if (!subredditName.equalsIgnoreCase("random"))  // Do not cache subreddit "random"
            subreddits.get(subredditName.toLowerCase()).posts.put(sortMethod, ps);
        return ps;
    }

    /**
     * Get the name of the subreddit
     *
     * @return String subreddit name
     */
    public String getSubredditName() {
        return subredditName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Subreddit subreddit = (Subreddit) o;

        return getSubredditName().equals(subreddit.getSubredditName());
    }

    @Override
    public int hashCode() {
        return getSubredditName().hashCode();
    }
}
