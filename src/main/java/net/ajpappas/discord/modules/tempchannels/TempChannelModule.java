package Techtony96.Discord.modules.tempchannels;

import Techtony96.Discord.api.CustomModule;
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
