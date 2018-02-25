package com.discordbolt.boltbot.system.twitch;

import com.discordbolt.boltbot.system.twitch.objects.TwitchUserDataResponse;
import com.discordbolt.boltbot.utils.Logger;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.zip.DataFormatException;

public class TwitchAPI {

    private String clientID, clientSecret;
    private final OkHttpClient client;
    private final Gson gson;
    private final TwitchUser user;
    private final TwitchClip clip;

    private Path propertiesPath = Paths.get(System.getProperty("user.dir"), "twitch.properties");
    private final String TWITCH_TOKEN_URL = "https://api.twitch.tv/kraken/oauth2/token";


    /**
     * Create a new instance of TwitchAPI
     * @throws DataFormatException Thrown when twitch.properties is not configured
     */
    public TwitchAPI() throws DataFormatException {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.clip = new TwitchClip(this);
        this.user = new TwitchUser(this);

        OutputStream output = null;
        try {
            if (!propertiesPath.toFile().exists()){
                Properties prop = new Properties();
                output = new FileOutputStream(propertiesPath.toFile());
                prop.setProperty("CLIENT_ID", "insert_id_here");
                prop.setProperty("CLIENT_SECRET", "insert_secret_here");
                prop.store(output, "BoltBot Twitch Configuration");
                throw new DataFormatException("Properties file was not configured, unable to make any requests");
            }
        } catch (IOException e){
            Logger.error(e.getMessage());
            Logger.debug(e);
        } finally {
            if (output != null){
                try {
                    output.close();
                } catch (IOException e){
                    Logger.error("Could not close Twitch Properties file.");
                    Logger.debug(e);
                }
            }
        }
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesPath.toFile()));
            clientID = props.getProperty("CLIENT_ID");
            clientSecret = props.getProperty("CLIENT_SECRET");
        } catch (IOException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
        }
    }

    public String getOAuthToken() {

    }

    public void refreshOAuthToken(String refreshToken) throws IOException {
        HttpUrl tokenURL = HttpUrl.parse(TWITCH_TOKEN_URL).newBuilder()
                .addQueryParameter("grant_type", "refresh_token")
                .addQueryParameter("refresh_token", refreshToken)
                .addQueryParameter("client_id", getClientID())
                .addQueryParameter("client_secret", getClientSecret()).build();
        Request request = new Request.Builder().url(tokenURL).build();
        try (Response response = getClient().newCall(request).execute()) {

        }
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret(){
        return clientSecret;
    }

    public String getAuthHeader(){
        return "Bearer " + getOAuthToken();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }

    public TwitchUser getUser() {
        return user;
    }

    public TwitchClip getClip() {
        return clip;
    }
}
