package com.discordbolt.boltbot.modules.dice;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.boltbot.system.CustomModule;
import com.discordbolt.boltbot.utils.ChannelUtil;
import com.discordbolt.boltbot.utils.EmbedUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Tony on 4/7/2017.
 */
public class DiceModule extends CustomModule implements IModule {

    private static final Pattern DIE_PATTERN = Pattern.compile("(?:([0-9]+)?[dD])?([0-9]+)");
    private static final Color EMBED_COLOR = new Color(81, 5, 43);

    public DiceModule(IDiscordClient client) {
        super(client, "Dice Module", "1.1");
    }

    @BotCommand(command = "roll", module = "Dice Module", description = "Roll a die!", usage = "Roll [#d##]", args = 2)
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

        if (cc.getChannel().getTopic().toLowerCase().contains("--print-rolls")) {
            EmbedObject embed = getDieEmbed(numDice, numSides);
            cc.replyWith(embed.title);
            cc.replyWith(embed);
        } else
            cc.replyWith(getDieEmbed(numDice, numSides));
    }

    @EventSubscriber
    public void onFlipRequest(MessageReceivedEvent e) {
        if (e.getAuthor().isBot())
            return;
        if (e.getMessage().getContent().toLowerCase().contains("flip a coin")) {
            ChannelUtil.sendMessage(e.getChannel(), getDieEmbed(1, 2));
        }
    }

    private static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static int[] roll(int numSides, int numDice) {
        int[] result = new int[numDice];
        for (int i = 0; i < numDice; i++) {
            result[i] = random(1, numSides);
        }
        return result;
    }

    private static EmbedObject getDieEmbed(int numDice, int numSides) {
        int[] results = roll(numSides, numDice);
        int total = Arrays.stream(results).sum();

        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(EMBED_COLOR);

        if (numSides == 2 && numDice == 1) {
            embed.withTitle(total == 1 ? "Heads" : "Tails");
        } else {
            embed.withTitle(Integer.toString(total));
        }

        if (numDice > 1) {
            embed.withDescription(EmbedUtil.ZERO_WIDTH_SPACE);
            embed.appendField(numDice + "d" + numSides, Arrays.stream(results).mapToObj(i -> ((Integer) i).toString()).collect(Collectors.joining(" + ")), false);
        } else {
            embed.withDescription(numDice + "d" + numSides);
        }
        embed.withThumbnail(getDieURL(numSides, total, 250));
        return embed.build();
    }


    /**
     * Get the URL of a specific die
     *
     * @param sides  The number of sides the die has.
     *               Valid input: 2, 6, 20
     * @param result The result which should be shown on the die
     * @param width  The width of the image returned
     *               Valid input: 250, 500, 1000
     * @return String URL of the die
     */
    private static String getDieURL(int sides, int result, int width) {
        width = 250; // Temporary while we only support width 250
        if (width != 250 && width != 500 && width != 1000)
            return "http://bolt.ajpappas.net/static/dice/defaultx250.png";
        if (sides != 2 && sides != 6 && sides != 20)
            return String.format("http://bolt.ajpappas.net/static/dice/defaultx%s.png", width);
        if (result > sides)
            return String.format("http://bolt.ajpappas.net/static/dice/d%s/%sx%s.png", sides, sides, width);
        return String.format("http://bolt.ajpappas.net/static/dice/d%s/%sx%s.png", sides, result, width);
    }
}
