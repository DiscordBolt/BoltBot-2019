package net.ajpappas.discord.modules.misc;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.modules.IModule;

import java.util.Arrays;
import java.util.List;

public class DadJokeModule extends CustomModule implements IModule {

    private final List<String> ImTriggers = Arrays.asList("im", "i'm", "i am");

    public DadJokeModule(IDiscordClient client) {
        super(client, "Dad Joke Module", "1.0");
    }

    @EventSubscriber
    public void onJoke(MessageReceivedEvent e) {
        if (e.getAuthor().isBot())
            return;

        for (String s : ImTriggers) {
            if (e.getMessage().getContent().toLowerCase().startsWith(s)) {
                ChannelUtil.sendMessage(e.getChannel(), "Hi " + e.getMessage().getContent().substring(s.length()).trim() + ", I am Discord.java, nice to meet you.");
                return;
            }
        }
    }
}
