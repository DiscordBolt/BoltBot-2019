package net.ajpappas.discord.api.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.ajpappas.discord.utils.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.file.Files;
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
        try {
            if (!prefixFile.exists()){
                Files.write(prefixFile.toPath(), Collections.singletonList("{}"), Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            Logger.error("Unable to create command prefix file.");
            Logger.debug(e);
        }
        // Load custom command prefixes
        try {
            prefixes = g.fromJson(new FileReader(prefixFile), new TypeToken<Map<Long, String>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            Logger.error("Command Prefix file does not exist!");
            Logger.debug(e);
        }

        Reflections ref = new Reflections("net.ajpappas.discord", new MethodAnnotationsScanner());
        Set<Method> commandMethods = ref.getMethodsAnnotatedWith(BotCommand.class);
        for (Method command : commandMethods) {
            BotCommand a = command.getAnnotation(BotCommand.class);
            if (!Modifier.isStatic(command.getModifiers())) {
                Logger.error("Command \"" + String.join(" ", a.command()) + "\" is not a static method!");
                continue;
            }
            commands.add(new CustomCommand(command));
        }

        commands.sort(new CommandComparator());
    }

    public static List<CustomCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public static String getCommandPrefix(IGuild guild) {
        if (guild == null)
            return DEFAULT_PREFIX;
        return prefixes.getOrDefault(guild.getLongID(), DEFAULT_PREFIX);
    }

    @BotCommand(command = "prefix", description = "Change the command prefix", usage = "Prefix [Character]", module = "Administration", args = 2, permissions = Permissions.ADMINISTRATOR)
    public static void setCommandPrefix(CommandContext cc) {
        prefixes.put(cc.getGuild().getLongID(), cc.getArgument(1).charAt(0) + "");
        writePrefixes();
        cc.replyWith("Your new custom prefix is `" + prefixes.getOrDefault(cc.getGuild().getLongID(), DEFAULT_PREFIX) + "`. All commands must start with your custom prefix.");
    }

    static class CommandComparator implements Comparator<CustomCommand> {
        public int compare(CustomCommand c1, CustomCommand c2) {
            return (c1.getModule() + " " + String.join(" ", c1.getCommands())).compareTo(c2.getModule() + " " + String.join(" ", c2.getCommands()));
        }
    }

    protected static void writePrefixes() {
        try {
            prefixFile.createNewFile();
            FileWriter fw = new FileWriter(prefixFile);
            fw.write(g.toJson(prefixes));
            fw.close();
        } catch (IOException e) {
            Logger.error("Unable to save command prefix file.");
            Logger.debug(e);
        }
    }
}
