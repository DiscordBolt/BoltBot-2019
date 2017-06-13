package net.ajpappas.discord.modules.tempchannels;

import net.ajpappas.discord.api.CustomModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

public class TempChannelModule extends CustomModule implements IModule {

    public TempChannelModule() {
        super("Temporary Channel Module", "1.1");
    }

    @Override
    public boolean enable(IDiscordClient client) {
        super.enable(client);
        client.getDispatcher().registerListener(new MessageHandler(client));
        return true;
    }
}
