package net.ajpappas.discord.modules.misc;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 4/7/2017.
 */
public class TableFixerModule extends CustomModule implements IModule {

    public TableFixerModule(IDiscordClient client) {
        super(client, "Table Fixer", "1.0");
    }

    @EventSubscriber
    public void onTableFlip(MessageReceivedEvent e) {
        if (e.getMessage().getContent().startsWith("(╯°□°）╯︵ ┻━┻")) {
            ChannelUtil.sendMessage(e.getChannel(), "┬─┬ノ(ಠ_ಠノ)");
        }
    }
}
