package com.discordbolt.boltbot.discord.modules.dice;

import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.api.CommandBean;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import com.discordbolt.boltbot.discord.util.EmbedUtil;
import discord4j.core.DiscordClient;
import discord4j.core.spec.EmbedCreateSpec;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DiceModule implements BotModule {

    private static final Color EMBED_COLOR = new Color(81, 5, 43);

    @Override
    public void initialize(DiscordClient client) {
        BeanUtil.getBean(CommandBean.class).registerCommand(new RollCommand());
    }

    protected static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    protected static int[] roll(int numSides, int numDice) {
        int[] result = new int[numDice];
        for (int i = 0; i < numDice; i++) {
            result[i] = random(1, numSides);
        }
        return result;
    }

    protected static Consumer<EmbedCreateSpec> getDieEmbed(int[] rollResult, int numSides) {
        int numDice = rollResult.length;
        int total = Arrays.stream(rollResult).sum();

        return embed -> {
            embed.setColor(EMBED_COLOR);

            if (numSides == 2 && numDice == 1) {
                embed.setTitle(total == 1 ? "Heads" : "Tails");
            } else {
                embed.setTitle(Integer.toString(total));
            }

            if (numDice > 1) {
                embed.setDescription(EmbedUtil.ZERO_WIDTH_SPACE);
                embed.addField(numDice + "d" + numSides, Arrays.stream(rollResult).mapToObj(i -> ((Integer) i).toString()).collect(Collectors.joining(" + ")), false);
            } else {
                embed.setDescription(numDice + "d" + numSides);
            }
            embed.setThumbnail(getDieURL(numSides, total, 250));
        };
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
