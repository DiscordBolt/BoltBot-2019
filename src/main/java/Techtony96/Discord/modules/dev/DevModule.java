package Techtony96.Discord.modules.dev;

import Techtony96.Discord.api.CustomModule;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 2/25/2017.
 */
public class DevModule extends CustomModule implements IModule {

    public DevModule() {
        super("Dev Module", "1.0");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        new ListRoles(client);
    }
}
