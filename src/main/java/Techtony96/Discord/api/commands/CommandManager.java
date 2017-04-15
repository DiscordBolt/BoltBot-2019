package Techtony96.Discord.api.commands;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import sx.blah.discord.modules.IModule;

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
            BotCommand a = command.getAnnotation(BotCommand.class);
            if (!Modifier.isStatic(command.getModifiers())) {
                Logger.severe("Command \"" + a.command().toUpperCase() + "\" is not a static method!");
                continue;
            }
            //          if (modules.stream().filter(x -> x.getName().equalsIgnoreCase(a.module())).findAny().isPresent()){
            //              Logger.severe("Command \"" + a.command().toUpperCase() + "\" does not have a valid module!");
            //              continue;
            //          } Modules load in some order, Commands may be loaded before
            commands.add(new CustomCommand(command));
        }

        commands.sort(new CommandComparator());
    }

    public static List<CustomCommand> getCommands() {
        return commands;
    }

    static class CommandComparator implements Comparator<CustomCommand> {
        public int compare(CustomCommand c1, CustomCommand c2) {
            return c1.getModule().compareTo(c2.getModule());
        }
    }
}
