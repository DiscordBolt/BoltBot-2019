package Techtony96.Discord.modules.dice;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.ExceptionMessage;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Created by Tony on 4/7/2017.
 */
public class DiceModule extends CustomModule implements IModule {

    private final Pattern DIE_PATTERN = Pattern.compile("([dD]*[0-9]+)");
    private final String HEADS_URL = "http://i.imgur.com/i68ZFNG.png";
    private final String TAILS_URL = "http://i.imgur.com/nml8Sfx.png";
    private final String DIE_URL_1 = "http://i.imgur.com/DVVjp7w.png";
    private final String DIE_URL_2 = "http://i.imgur.com/VcRv20t.png";
    private final String DIE_URL_3 = "http://i.imgur.com/rUdUe5L.png";
    private final String DIE_URL_4 = "http://i.imgur.com/1JGHyXF.png";
    private final String DIE_URL_5 = "http://i.imgur.com/KYgqzWC.png";
    private final String DIE_URL_6 = "http://i.imgur.com/Exx1Pmz.png";

    public DiceModule() {
        super("Dice Module", "1.0");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        new BotCommand(client, "roll") {

            @Override
            public void execute(CommandContext cc) {
                for (String arg : cc.getArguments()) {
                    if (DIE_PATTERN.matcher(arg).find()) {
                        int die = -1;
                        try {
                            die = Integer.valueOf(arg.replaceAll("([dD])", ""));
                        } catch (NumberFormatException e) {
                            cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
                            return;
                        }

                        if (die < 2) {
                            cc.replyWith(die + " is not a valid number.");
                        } else if (die == 2) {
                            cc.replyWith(getCoinEmbed());
                        } else {
                            cc.replyWith(getDieEmbed(die));
                        }
                        return;
                    }
                }
            }
        }.setUsage("!Roll d##").setArguments(2);
    }

    @EventSubscriber
    public void onFlipRequest(MessageReceivedEvent e) {
        if (e.getMessage().getContent().toLowerCase().contains("flip a coin")) {
            ChannelUtil.sendMessage(e.getChannel(), getCoinEmbed());
        }
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private EmbedObject getCoinEmbed() {
        boolean heads = random(1, 2) == 1;

        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(255, 235, 59); // Hex #FFEB3B (Yellow)
        embed.withTitle(heads ? "Heads" : "Tails");
        embed.withThumbnail(heads ? HEADS_URL : TAILS_URL);
        return embed.build();
    }

    private EmbedObject getDieEmbed(int max) {
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
}
