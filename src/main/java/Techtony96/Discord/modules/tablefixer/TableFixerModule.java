package Techtony96.Discord.modules.tablefixer;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.utils.ChannelUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 4/7/2017.
 */
public class TableFixerModule extends CustomModule implements IModule {

    public TableFixerModule() {
        super("Table Fixer", "1.0");
    }

    @EventSubscriber
    public void onTableFlip(MessageReceivedEvent e) {
        if (e.getMessage().getContent().startsWith("(╯°□°）╯︵ ┻━┻")) {
            ChannelUtil.sendMessage(e.getChannel(), "┬─┬ノ(ಠ_ಠノ)");
        }
    }
}
