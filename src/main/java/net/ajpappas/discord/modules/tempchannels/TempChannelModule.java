package net.ajpappas.discord.modules.tempchannels;

import net.ajpappas.discord.api.CustomModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

public class TempChannelModule extends CustomModule implements IModule {

    public TempChannelModule(IDiscordClient client) {
        super(client, "Temporary Channel Module", "1.1");
        client.getDispatcher().registerListener(new MessageHandler(client));
    }
}
