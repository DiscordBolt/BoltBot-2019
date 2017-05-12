package Techtony96.Discord.api.commands;

import Techtony96.Discord.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Tony on 2/17/2017.
 */
public class CommandManager {

    private static final Gson g = new Gson();
    private static final String DEFAULT_PREFIX = "!";
    private static final File prefixFile = Paths.get(System.getProperty("user.dir"), "CommandPrefixes.json").toFile();

    private static List<CustomCommand> commands = new ArrayList<>();
    private static HashMap<Long, String> prefixes = new HashMap<>();


    // Don't make instances of this class
    private CommandManager() {

    }

    public static void initializeCommands() {
        // Load custom command prefixes
        try {
            prefixes = g.fromJson(new FileReader(prefixFile), new TypeToken<Map<Long, String>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            Logger.error("Prefix file does not exist!");
            Logger.debug(e);
        }


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

    public static String getCommandPrefix(IGuild guild) {
        return prefixes.getOrDefault(guild.getLongID(), DEFAULT_PREFIX);
    }

    @BotCommand(command = "prefix", description = "Change the command prefix", usage = "Prefix [Character]", module = "dev", args = 2, permissions = Permissions.ADMINISTRATOR)
    public static void setCommandPrefix(CommandContext cc) {
        prefixes.put(cc.getGuild().getLongID(), cc.getArgument(1).charAt(0) + "");
        writePrefixes();
        cc.replyWith("Your new custom prefix is `" + prefixes.getOrDefault(cc.getGuild().getLongID(), DEFAULT_PREFIX) + "`. All commands must start with your custom prefix.");
    }

    static class CommandComparator implements Comparator<CustomCommand> {
        public int compare(CustomCommand c1, CustomCommand c2) {
            return c1.getModule().compareTo(c2.getModule());
        }
    }

    protected static void writePrefixes() {
        try {
            prefixFile.createNewFile();
            FileWriter fw = new FileWriter(prefixFile);
            fw.write(g.toJson(prefixes));
            fw.close();
        } catch (IOException e) {
            Logger.error("Unable to save prefix file.");
            Logger.debug(e);
        }
    }
}
