package Techtony96.Discord.api.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tony on 2/17/2017.
 */
public class CommandManager {

    private static List<BotCommand> commands = new ArrayList<>();

    // Don't make instances of this class
    private CommandManager() {

    }

    public static void register(BotCommand command) {
        commands.add(command);
        Collections.sort(commands, new CommandComparator());
    }

    public static List<BotCommand> getCommands() {
        return commands;
    }

    static class CommandComparator implements Comparator<BotCommand> {
        public int compare(BotCommand c1, BotCommand c2) {
            return c1.getName().compareTo(c2.getName());
        }
    }
}
