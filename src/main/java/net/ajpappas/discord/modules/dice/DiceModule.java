package net.ajpappas.discord.modules.dice;

import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandArgumentException;
import net.ajpappas.discord.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tony on 4/7/2017.
 */
public class DiceModule extends CustomModule implements IModule {

    private static final Pattern DIE_PATTERN = Pattern.compile("(?:([0-9]+)?[dD])?([0-9]+)");
    private static final String HEADS_URL = "http://i.imgur.com/i68ZFNG.png";
    private static final String TAILS_URL = "http://i.imgur.com/nml8Sfx.png";
    private static final String DIE_URL_1 = "http://i.imgur.com/DVVjp7w.png";
    private static final String DIE_URL_2 = "http://i.imgur.com/VcRv20t.png";
    private static final String DIE_URL_3 = "http://i.imgur.com/rUdUe5L.png";
    private static final String DIE_URL_4 = "http://i.imgur.com/1JGHyXF.png";
    private static final String DIE_URL_5 = "http://i.imgur.com/KYgqzWC.png";
    private static final String DIE_URL_6 = "http://i.imgur.com/Exx1Pmz.png";

    public DiceModule(IDiscordClient client) {
        super(client, "Dice Module", "1.1");
    }

    @BotCommand(command = "roll", module = "Dice Module", description = "Roll a die!", usage = "Roll #d##", args = 2)
    public static void rollCommand(CommandContext cc) throws CommandArgumentException {
        Matcher m = DIE_PATTERN.matcher(cc.getArgument(1));

        if (!m.matches()) {
            cc.replyWith("Your die was not formatted correctly. !Roll #d##");
            return;
        }

        int numDice = Integer.valueOf(Optional.ofNullable(m.group(1)).orElse("1"));
        int numSides = Integer.valueOf(Optional.ofNullable(m.group(2)).orElseThrow(() -> new CommandArgumentException("Your die was not formatted correctly. !Roll #d##")));

        if (numDice < 1 || numSides <= 1 || numDice > 100 || numSides > 100) {
            cc.replyWith("Your die has an invalid number of sides.");
            return;
        }

        cc.replyWith(getDieEmbed(numDice, numSides));
    }

    @EventSubscriber
    public void onFlipRequest(MessageReceivedEvent e) {
        if (e.getMessage().getContent().toLowerCase().contains("flip a coin")) {
            ChannelUtil.sendMessage(e.getChannel(), getCoinEmbed());
        }
    }

    private static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static EmbedObject getCoinEmbed() {
        boolean heads = random(1, 2) == 1;

        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(255, 235, 59); // Hex #FFEB3B (Yellow)
        embed.withTitle(heads ? "Heads" : "Tails");
        embed.withThumbnail(heads ? HEADS_URL : TAILS_URL);
        return embed.build();
    }

    private static EmbedObject getDieEmbed(int numDice, int numSides) {
        int result = 0;

        for (int i = 0; i < numDice; i++)
            result += random(1, numSides);

        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(255, 235, 59); // Hex #FFEB3B (Yellow)

        embed.withTitle(Integer.toString(result));
        embed.withDescription(numDice + "d" + numSides);
        switch (result) {
            case 1:
                embed.withThumbnail(DIE_URL_1);
                break;
            case 2:
                embed.withThumbnail(DIE_URL_2);
                break;
            case 3:
                embed.withThumbnail(DIE_URL_3);
                break;
            case 4:
                embed.withThumbnail(DIE_URL_4);
                break;
            case 5:
                embed.withThumbnail(DIE_URL_5);
                break;
            default:
                embed.withThumbnail(DIE_URL_6);
                break;
        }
        return embed.build();
    }
}
