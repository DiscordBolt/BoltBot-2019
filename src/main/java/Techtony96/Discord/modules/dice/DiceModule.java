package Techtony96.Discord.modules.dice;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.ExceptionMessage;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.EmbedBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Created by Tony on 4/7/2017.
 */
public class DiceModule extends CustomModule implements IModule {

    private static final Pattern DIE_PATTERN = Pattern.compile("([dD]*[0-9]+)");
    private static final String HEADS_URL = "http://i.imgur.com/i68ZFNG.png";
    private static final String TAILS_URL = "http://i.imgur.com/nml8Sfx.png";
    private static final String DIE_URL_1 = "http://i.imgur.com/DVVjp7w.png";
    private static final String DIE_URL_2 = "http://i.imgur.com/VcRv20t.png";
    private static final String DIE_URL_3 = "http://i.imgur.com/rUdUe5L.png";
    private static final String DIE_URL_4 = "http://i.imgur.com/1JGHyXF.png";
    private static final String DIE_URL_5 = "http://i.imgur.com/KYgqzWC.png";
    private static final String DIE_URL_6 = "http://i.imgur.com/Exx1Pmz.png";

    public DiceModule() {
        super("Dice Module", "1.0");
    }

    @BotCommand(command = "roll", module = "Dice Module", description = "Roll a die!", usage = "Roll d##")
    public static void rollCommand(CommandContext cc) {
        for (String arg : cc.getArguments()) {
            if (DIE_PATTERN.matcher(arg).find()) {
                int count = -1;
                int die;
                final String[] split = arg.split("([dD])");

                if (split.length == 2) {
                    count = Integer.valueOf(split[0]) != null ? Integer.valueOf(split[0]) : 1;
                    die = Integer.valueOf(split[1]);
                } else {
                    try {
                        die = Integer.valueOf(arg.replaceAll("([dD])", ""));
                    } catch (NumberFormatException e) {
                        cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
                        return;
                    }
                }

                if (die < 2) {
                    cc.replyWith(die + " is not a valid number.");
                } else if (die == 2) {
                    cc.replyWith(getCoinEmbed());
                } else {
                    if (count > 0) {
                        cc.replyWith(getMultiDieEmbed(count, die));
                    } else {
                        cc.replyWith(getDieEmbed(die));
                    }
                }
                return;
            }
        }
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

    private static EmbedObject getDieEmbed(int max) {
        int result = random(1, max);

        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(255, 235, 59); // Hex #FFEB3B (Yellow)

        embed.withTitle(Integer.toString(result));
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

    private static EmbedObject getMultiDieEmbed(int count, int max) {

        int total = 0;
        for (int i = 0; i < count; i++) {
            total += random(1, max);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(255, 235, 59); // Hex #FFEB3B (Yellow)

        embed.withTitle(count + "d" + max + "== " + total);
        embed.withThumbnail(DIE_URL_6);
        return embed.build();
    }
}
