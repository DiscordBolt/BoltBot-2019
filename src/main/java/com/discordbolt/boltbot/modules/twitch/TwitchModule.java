package com.discordbolt.boltbot.modules.twitch;

import com.discordbolt.boltbot.system.CustomModule;
import com.discordbolt.boltbot.system.twitch.TwitchAPI;
import com.discordbolt.boltbot.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

import java.util.zip.DataFormatException;

public class TwitchModule extends CustomModule implements IModule {

    public TwitchModule(IDiscordClient client) {
        super(client, "Twitch Module", "2.0");
        client.getDispatcher().registerListener(new StreamAnnouncer(client));
    }

    public static TwitchAPI getTwitchAPI() {
        try {
            return TwitchAPI.getInstance();
        } catch (DataFormatException e) {
            Logger.error("Error initializing Twitch API.");
            Logger.debug(e);
            return null;
        }
    }
}
