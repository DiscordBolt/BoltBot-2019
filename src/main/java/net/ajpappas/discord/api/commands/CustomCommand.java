package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.utils.ExceptionMessage;
import net.ajpappas.discord.utils.Logger;
import net.ajpappas.discord.utils.UserUtil;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Tony on 2/15/2017.
 */
public class CustomCommand {

    private String[] command;
    private Method method;
    private String module;
    private String description;
    private String usage;
    private Set<String> aliases = new HashSet<>();
    private Set<String> allowedChannels = new HashSet<>();
    private EnumSet<Permissions> permissions = EnumSet.noneOf(Permissions.class);
    private int args = -1, minArgs = -1, maxArgs = -1;
    private boolean secret = false;
    private boolean allowPM = false;
    private boolean delete = false;

    protected CustomCommand(Method method) {
        CommandModule.getClient().getDispatcher().registerListener(this);

        BotCommand annotation = method.getAnnotation(BotCommand.class);

        this.command = Arrays.stream(annotation.command()).map(String::toLowerCase).toArray(String[]::new);
        this.method = method;
        this.module = annotation.module();
        this.description = annotation.description();
        this.usage = annotation.usage();
        this.aliases.addAll(Arrays.asList(annotation.aliases()));
        this.allowedChannels.addAll(Arrays.asList(annotation.allowedChannels()));
        this.permissions.addAll(Arrays.asList(annotation.permissions()));

        if (annotation.args() != -1)
            setArguments(annotation.args());
        if (annotation.minArgs() != -1 && annotation.maxArgs() != -1)
            setArguments(annotation.minArgs(), annotation.maxArgs());

        this.secret = annotation.secret();
        this.allowPM = annotation.allowPM();
        this.delete = annotation.deleteMessages();
    }

    private void setArguments(int arguments) {
        if (arguments < 0)
            throw new IllegalArgumentException("Arguments must be > 0");
        args = arguments;
    }

    private void setArguments(int minArgs, int maxArgs) {
        if (minArgs < 0 || maxArgs < 0)
            throw new IllegalArgumentException("Arguments must be > 0");
        if (maxArgs - minArgs < 0)
            throw new IllegalArgumentException("maxArgs must be larger than minArgs");
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    public String getBaseCommand() {
        return command[0];
    }

    public String[] getCommands() {
        return command;
    }

    public String getCommand(int index) {
        return command[index];
    }

    public String getModule() {
        return module;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage(IGuild guild) {
        return CommandManager.getCommandPrefix(guild) + usage;
    }

    public List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    public List<String> getAllowedChannels() {
        return new ArrayList<>(allowedChannels);
    }

    public EnumSet<Permissions> getPermissions() {
        return EnumSet.copyOf(permissions);
    }

    public boolean isSecret() {
        return secret;
    }

    public boolean isAllowPM() {
        return allowPM;
    }

    public boolean shouldDeleteMessages() {
        return delete;
    }

    public void preexec(IMessage message, IUser user) {

        CommandContext cc = new CommandContext(message, this);

        // Check if the command should respond to PMs
        if (!isAllowPM() && cc.isPrivateMessage()) {
            return;
        }

        if (!(cc.isPrivateMessage() && isAllowPM()) && getAllowedChannels().size() > 0 && !getAllowedChannels().contains(cc.getChannel().getName())) {
            cc.replyWith(cc.getUserBaseCommand() + " can not be executed in " + cc.getChannel().mention());
            return;
        }

        // Argument count check
        if (args != -1) {
            if (cc.getArgCount() != args) {
                cc.replyWith("Incorrect Argument Count." + (getUsage(message.getGuild()).length() > 0 ? " Usage: " + getUsage(message.getGuild()) : ""));
                return;
            }
        }

        if (minArgs != -1 && maxArgs != -1) {
            if (cc.getArgCount() < minArgs || cc.getArgCount() > maxArgs) {
                cc.replyWith("Incorrect Argument Count." + (getUsage(message.getGuild()).length() > 1 ? " Usage: " + getUsage(message.getGuild()) : ""));
                return;
            }
        }

        // Permission check
        if (!UserUtil.isBotOwner(cc.getAuthor())) {
            if (message.getChannel().isPrivate() && getPermissions().size() != 0) {
                cc.replyWith(ExceptionMessage.EXECUTE_IN_GUILD);
                return;
            }

            for (Permissions p : getPermissions()) {
                if (!user.getPermissionsForGuild(message.getGuild()).contains(p)) {
                    cc.replyWith(cc.getAuthor().mention() + " " + ExceptionMessage.PERMISSION_DENIED);
                    return;
                }
            }
        }

        try {
            method.invoke(null, cc);
        } catch (InvocationTargetException ite) {
            if (ite.getCause() instanceof CommandException) {
                cc.replyWith(ite.getCause().getMessage());
            } else {
                Logger.error("Uncaught exception during execution of \"" + CommandManager.getCommandPrefix(cc.getGuild()) + String.join(" ", getCommands()) + "\" command.");
                Logger.error(ite.getCause().getMessage());
                Logger.debug(ite.getCause());
                cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
            }
        } catch (IllegalAccessException ex) {
            Logger.error(ex.getMessage());
            Logger.debug(ex);
            cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
        }

        if (shouldDeleteMessages()) {
            message.delete();
        }
    }

    public boolean matches(String userCommand) {
        String userBaseCommand = userCommand.substring(1, userCommand.indexOf(" ") > 0 ? userCommand.indexOf(" ") : userCommand.length());

        for (int i = 0; i < getCommands().length; i++) {
            if (i == 0) {  // Checking the base command
                if (!(getBaseCommand().equalsIgnoreCase(userBaseCommand) || (getAliases().size() > 0 && getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(userBaseCommand)))))
                    return false;
            } else {  // Check the sub commands
                if (!getCommand(i).equalsIgnoreCase(userCommand.split(" ")[i]))
                    return false;
            }
        }
        return true;
    }
}
