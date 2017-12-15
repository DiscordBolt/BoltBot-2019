package com.discordbolt.boltbot.api;

import com.discordbolt.boltbot.utils.Logger;
import sx.blah.discord.api.IDiscordClient;

/**
 * Created by Tony on 12/24/2016.
 */
public abstract class CustomModule {

    protected static IDiscordClient client;
    private String moduleName;
    private String moduleVersion;
    private String moduleMinimumVersion = "2.7.0";
    private String author = "Techtony96";

    public CustomModule(IDiscordClient client, String moduleName, String moduleVersion) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        Logger.info("[BoltBot] " + getName() + " version " + getVersion() + " is initializing.");
        enable(client);
    }

    public CustomModule(IDiscordClient client, String moduleName, String moduleVersion, String moduleMinimumVersion, String author) {
        this(client, moduleName, moduleVersion);
        this.moduleMinimumVersion = moduleMinimumVersion;
        this.author = author;
    }

    public void disable() {
        Logger.info("[BoltBot] " + getName() + " version " + getVersion() + " is disabled.");
    }

    public boolean enable(IDiscordClient client) {
        this.client = client;
        return true;
    }

    public String getAuthor() {
        return author;
    }

    public String getMinimumDiscord4JVersion() {
        return moduleMinimumVersion;
    }

    public String getName() {
        return moduleName;
    }

    public String getVersion() {
        return moduleVersion;
    }

    public static IDiscordClient getClient() {
        return client;
    }
}
