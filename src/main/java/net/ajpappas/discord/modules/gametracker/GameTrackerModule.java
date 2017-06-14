package net.ajpappas.discord.modules.gametracker;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.mysql.MySQL;
import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony Pappas on 9/29/2016.
 */
public class GameTrackerModule extends CustomModule implements IModule {

    protected static final int MIN_GAME_TIME = 15 * 60 * 1000; //15 Minutes

    public GameTrackerModule(IDiscordClient client) {
        super(client, "GameTracker", "1.1");
        if (MySQL.isConnected()){
            getClient().getDispatcher().registerListener(new GameListener());
        } else {
            Logger.error("Unable to connect to MySQL database. Disabling Game Tracker.");
        }
    }
}
