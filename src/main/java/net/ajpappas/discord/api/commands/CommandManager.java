package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.api.mysql.data.persistent.GuildData;
import net.ajpappas.discord.utils.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Tony on 2/17/2017.
 */
public class CommandManager {

    private static final String DEFAULT_PREFIX = "!";
    private static List<CustomCommand> commands = new ArrayList<>();

    // Don't make instances of this class
    private CommandManager() {

    }

    public static void initializeCommands() {
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

        new CommandListener();
    }

    public static List<CustomCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public static String getCommandPrefix(IGuild guild) {
        if (guild == null)
            return DEFAULT_PREFIX;
        return GuildData.getById(guild.getLongID()).map(GuildData::getCommandPrefix).orElse(DEFAULT_PREFIX);
    }

    @BotCommand(command = "prefix", description = "Change the command prefix", usage = "Prefix [Character]", module = "Administration", args = 2, permissions = Permissions.ADMINISTRATOR)
    public static void setCommandPrefix(CommandContext cc) {
        GuildData.getById(cc.getGuild().getLongID()).ifPresent(g -> g.setCommandPrefix(cc.getArgument(1).charAt(0) + ""));
        cc.replyWith("Your new custom prefix is `" + getCommandPrefix(cc.getGuild()) + "`. All commands must start with your new prefix.");
    }

    static class CommandComparator implements Comparator<CustomCommand> {
        public int compare(CustomCommand c1, CustomCommand c2) {
            return (c1.getModule() + " " + String.join(" ", c1.getCommands())).compareTo(c2.getModule() + " " + String.join(" ", c2.getCommands()));
        }
    }
}
