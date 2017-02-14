package Techtony96.Discord.modules.help;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.ChannelUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 12/24/2016.
 */
public class HelpModule extends CustomModule implements IModule {


    public HelpModule() {
        super("Help Command", "1.1");
    }

    @EventSubscriber
    public void OnMesageEvent(MessageReceivedEvent e) {
        IMessage message = e.getMessage();
        IChannel channel = message.getChannel();
        String messageString = message.getContent();
        String[] args = messageString.split(" ");
        String command = args[0].toLowerCase();

        if (command.equals("!help")) {
            ChannelUtil.sendMessage(channel, "!Disconnect [User(s)] | Disconnect one or more users from a voice channel.");
            ChannelUtil.sendMessage(channel, "!Create <-Private> [Channel Name]");
        }
    }
}
