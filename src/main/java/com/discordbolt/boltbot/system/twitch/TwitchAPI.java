package com.discordbolt.boltbot.system.twitch;

import com.discordbolt.boltbot.system.twitch.objects.TwitchAccessToken;
import com.discordbolt.boltbot.utils.Logger;
import com.discordbolt.boltbot.utils.PropertiesUtil;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.zip.DataFormatException;

public class TwitchAPI {

    private static TwitchAPI instance;

    private String clientID, clientSecret;
    private String accessToken, refreshToken;
    private final OkHttpClient client;
    private final Gson gson;
    private final TwitchUser user;
    private final TwitchClip clip;

    private Path propertiesPath = Paths.get(System.getProperty("user.dir"), "properties", "twitch.properties");
    private final String TWITCH_TOKEN_URL = "https://api.twitch.tv/kraken/oauth2/token";
    public static final int UNAUTHORIZED_RESPONSE_CODE = 401;


    /**
     * Create a new instance of TwitchAPI
     *
     * @throws DataFormatException Thrown when twitch.properties is not configured
     */
    TwitchAPI() throws DataFormatException {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.clip = new TwitchClip(this);
        this.user = new TwitchUser(this);

        OutputStream output = null;
        try {
            if (!propertiesPath.toFile().exists()) {
                Properties prop = new Properties();
                output = new FileOutputStream(propertiesPath.toFile());
                prop.setProperty("CLIENT_ID", "insert_id_here");
                prop.setProperty("CLIENT_SECRET", "insert_secret_here");
                prop.setProperty("ACCESS_TOKEN", "create_access_token");
                prop.setProperty("REFRESH_TOKEN", "create_refresh_token");
                prop.store(output, "BoltBot Twitch Configuration");
                throw new DataFormatException("Properties file was not configured, unable to make any requests");
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
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
            accessToken = props.getProperty("ACCESS_TOKEN");
            refreshToken = props.getProperty("REFRESH_TOKEN");
        } catch (IOException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
        }
    }

    public static TwitchAPI getInstance() throws DataFormatException {
        if (instance == null)
            instance = new TwitchAPI();

        return instance;
    }

    public void refreshOAuthToken() throws Exception {
        Logger.warning("Refreshing Twitch OAuth Token");
        HttpUrl tokenURL = HttpUrl.parse(TWITCH_TOKEN_URL).newBuilder()
                .addQueryParameter("grant_type", "refresh_token")
                .addQueryParameter("refresh_token", getRefreshToken())
                .addQueryParameter("client_id", getClientID())
                .addQueryParameter("client_secret", getClientSecret()).build();
        Request request = new Request.Builder().post(RequestBody.create(null, new byte[0])).url(tokenURL).build();
        try (Response response = getClient().newCall(request).execute()) {
            TwitchAccessToken newTokenData = getGson().fromJson(response.body().string(), TwitchAccessToken.class);
            if (newTokenData.getAccessToken() == null || newTokenData.getAccessToken().length() < 1)
                throw new Exception("Unable to refresh Twitch access token"); //TODO make this a better exception

            try {
                setAccessToken(newTokenData.getAccessToken());
                setRefreshToken(newTokenData.getRefreshToken());
            } catch (IOException e) {
                Logger.error("Unable to update Twitch properties file with new access and refresh token");
                Logger.debug(e);
            }
        }
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAuthHeader() {
        return "Bearer " + getAccessToken();
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

    private void setAccessToken(String newAccessToken) throws IOException {
        this.accessToken = newAccessToken;
        PropertiesUtil.updateField(propertiesPath, "ACCESS_TOKEN", newAccessToken);
    }

    private void setRefreshToken(String newRefreshToken) throws IOException {
        this.refreshToken = newRefreshToken;
        PropertiesUtil.updateField(propertiesPath, "REFRESH_TOKEN", newRefreshToken);
    }
}
