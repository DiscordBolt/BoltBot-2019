package Techtony96.Discord.api;

import Techtony96.Discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

/**
 * Created by Tony on 12/24/2016.
 */
public abstract class CustomModule {

    protected IDiscordClient client;
    private String moduleName = "Custom Module";
    private String moduleVersion = "1.0";
    private String moduleMinimumVersion = "2.7.0";
    private String author = "Techtony96";

    public CustomModule(String moduleName, String moduleVersion){
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }

    public CustomModule(String moduleName, String moduleVersion, String moduleMinimumVersion, String author){
        this(moduleName, moduleVersion);
        this.moduleMinimumVersion = moduleMinimumVersion;
        this.author = author;
    }


    public void disable() {
        Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is disabled.");
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


    public String getVersion() { return moduleVersion; }

    public IDiscordClient getClient() { return client; }

    @EventSubscriber
    public void onReady(ReadyEvent e) { Logger.info("[Discord.java] " + getName() + " version " + getVersion() + " is READY."); }
}
