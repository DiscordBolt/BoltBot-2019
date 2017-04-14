package Techtony96.Discord.api.commands;

import Techtony96.Discord.utils.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by Tony on 2/17/2017.
 */
public class CommandManager {

    private static List<CustomCommand> commands = new ArrayList<>();

    // Don't make instances of this class
    private CommandManager() {

    }

    public static void initializeCommands() {
        Reflections ref = new Reflections("Techtony96.Discord", new MethodAnnotationsScanner());
        Set<Method> commandMethods = ref.getMethodsAnnotatedWith(BotCommand.class);
        for (Method command : commandMethods) {
            if (!Modifier.isStatic(command.getModifiers())) {
                Logger.severe("Command \"" + command.getAnnotation(BotCommand.class).command().toUpperCase() + "\" is not a static method!");
                continue;
            }
            commands.add(new CustomCommand(command));
        }

        commands.sort(new CommandComparator());
    }

    public static List<CustomCommand> getCommands() {
        return commands;
    }

    static class CommandComparator implements Comparator<CustomCommand> {
        public int compare(CustomCommand c1, CustomCommand c2) {
            return c1.getName().compareTo(c2.getName());
        }
    }
}
