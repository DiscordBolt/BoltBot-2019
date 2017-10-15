package net.ajpappas.discord.modules.reddit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ajpappas.discord.modules.reddit.internal.RawRedditObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Subreddit {

    private static Set<Subreddit> subreddits = new HashSet<>();
    private static OkHttpClient client = new OkHttpClient();
    private static JsonParser parser = new JsonParser();
    private static Gson gson = new Gson();
    public static final int CACHE_TTL = 30; // Number of minutes we won't send a new request to reddit for

    private String subredditName;
    private HashMap<SortMethod, PostList> posts = new HashMap<>();

    private Subreddit(String subredditName) {
        this.subredditName = subredditName.toLowerCase();

        // Cache our object in the HashSet. We are only allowed to store a single instance of a given subreddit.
        subreddits.add(this);
    }

    /**
     * Get the first 25 posts of a given subreddit with given sorting method
     *
     * @param subredditName Subreddit string to fetch posts of. No formatting.
     * @param sortMethod How you want reddit to sort the returned posts
     * @return List of raw reddit objects
     * @throws IOException If response fails or other IOException occurs
     */
    private static PostList getPosts(String subredditName, SortMethod sortMethod) throws IOException {
        // Check the cache first
        Optional<Subreddit> subredditSerach = subreddits.stream().filter(s -> s.getSubredditName().equalsIgnoreCase(subredditName)).findAny();
        if (subredditSerach.isPresent()) {
            // Subreddit exists in cache
            Subreddit subreddit = subredditSerach.get();
            if (subreddit.posts.containsKey(sortMethod)) {
                // Sort method exists in subreddit
                if (subreddit.posts.get(sortMethod).isCacheValid()) {
                    // Cache hit
                    return subreddit.posts.get(sortMethod);
                }
            }
        }

        // Cache miss, request the info from Github
        Request request = new Request.Builder().url("https://www.reddit.com/r/" + subredditName + sortMethod).build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful())
            throw new HttpResponseException(response.code(), response.message());

        JsonObject json = parser.parse(response.body().string()).getAsJsonObject();

        RawRedditObject[] rawRedditObjects = gson.fromJson(json.getAsJsonObject("data").getAsJsonArray("children"), RawRedditObject[].class);
        RedditPost[] redditPosts = new RedditPost[rawRedditObjects.length];

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
