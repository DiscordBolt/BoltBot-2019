package com.discordbolt.boltbot.discord.modules.dice;

import com.discordbolt.api.commands.CommandContext;
import com.discordbolt.api.commands.CustomCommand;
import com.discordbolt.api.commands.exceptions.CommandArgumentException;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollCommand extends CustomCommand {

    private static final Pattern DIE_PATTERN = Pattern.compile("(?:([0-9]+)?[dD])?([0-9]+)");

    private static String[] command = {"roll"};
    private static String description = "Roll some dice";
    private static String usage = "Roll #d##";
    private static String module = "Dice";

    public RollCommand() {
        super(command, description, usage, module);
        super.setAllowDM(true);
        super.setMinArgumentCount(2);
    }

    @Override
    public void execute(CommandContext commandContext) throws CommandArgumentException {
        String dieCommand = commandContext.combineArgs(1, commandContext.getArgCount() - 1);
        Matcher m = DIE_PATTERN.matcher(dieCommand);

        if (!m.matches()) {
            throw new CommandArgumentException("'" + dieCommand + "' did not match expected input!");
        }

        int numDice = Integer.valueOf(Optional.ofNullable(m.group(1)).orElse("1"));
        int numSides = Integer.valueOf(Optional.ofNullable(m.group(2)).orElseThrow(() -> new CommandArgumentException("Your die was not formatted correctly. !Roll #d##")));

        if (numDice < 1 || numSides <= 1 || numDice > 100 || numSides > 100) {
            commandContext.replyWith("Your die has an invalid number of sides.").subscribe();
            return;
        }

        EmbedCreateSpec embed = DiceModule.getDieEmbed(numDice, numSides);
        String embedTitle = "asdf"; //TODO Get the actual total roll from the embed

        commandContext.getChannel()
                .ofType(TextChannel.class)
                .filter(textChannel -> textChannel.getTopic().toLowerCase().contains("--print-rolls"))
                .flatMap(c -> commandContext.replyWith(embedTitle, embed))
                .switchIfEmpty(commandContext.replyWith(embed))
                .subscribe();
    }
}
