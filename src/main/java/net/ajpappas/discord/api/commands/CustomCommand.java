package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.utils.ExceptionMessage;
import net.ajpappas.discord.utils.Logger;
import net.ajpappas.discord.utils.UserUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.PrivateChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

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

    @EventSubscriber
    public void onMesageEvent(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        IUser user = e.getAuthor();

        // Ignore bots
        if (user.isBot()) {
            return;
        }

        // Check if the command should respond to PMs
        if (!isAllowPM() && e.getChannel() instanceof PrivateChannel) {
            return;
        }

        // Message is just a single prefix.
        if (message.length() <= 1)
            return;

        // Check if message typed was a command
        if (!message.startsWith(CommandManager.getCommandPrefix(e.getGuild())))
            return;

        // Check if command was this command
        CommandContext cc = new CommandContext(e.getMessage(), this);

        // Check if there are enough arguments
        if (getCommands().length > cc.getArgCount())
            return;

        // Check if all the arguments match the expected commands
        for (int i = 0; i < getCommands().length; i++) {
            if (i == 0) {  // Checking the base command
                if (!(getBaseCommand().equalsIgnoreCase(cc.getUserBaseCommand()) || (getAliases().size() > 0 && getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(cc.getUserBaseCommand())))))
                    return;
            } else {  // Check the sub commands
                if (!getCommand(i).equalsIgnoreCase(cc.getArgument(i)))
                    return;
            }
        }

        // User command matches THIS custom command
        Logger.debug("User command \"" + cc.getMessageContent() + "\" matches expected command \"" + CommandManager.getCommandPrefix(cc.getGuild()) + String.join(" ", getCommands()) + "\"");

        if (!(cc.isPrivateMessage() && isAllowPM()) && getAllowedChannels().size() > 0 && !getAllowedChannels().contains(cc.getChannel().getName())) {
            cc.replyWith(cc.getUserBaseCommand() + " can not be executed in " + cc.getChannel().mention());
            return;
        }

        // Argument count check
        if (args != -1) {
            if (cc.getArgCount() != args) {
                cc.replyWith("Incorrect Argument Count." + (getUsage(e.getGuild()).length() > 0 ? " Usage: " + getUsage(e.getGuild()) : ""));
                return;
            }
        }

        if (minArgs != -1 && maxArgs != -1) {
            if (cc.getArgCount() < minArgs || cc.getArgCount() > maxArgs) {
                cc.replyWith("Incorrect Argument Count." + (getUsage(e.getGuild()).length() > 1 ? " Usage: " + getUsage(e.getGuild()) : ""));
                return;
            }
        }

        // Permission check
        if (!UserUtil.isBotOwner(cc.getAuthor())) {
            if (e.getChannel() instanceof PrivateChannel && getPermissions().size() != 0) {
                cc.replyWith(ExceptionMessage.EXECUTE_IN_GUILD);
                return;
            }

            for (Permissions p : getPermissions()) {
                if (!user.getPermissionsForGuild(e.getGuild()).contains(p)) {
                    cc.replyWith(cc.getAuthor().mention() + " " + ExceptionMessage.PERMISSION_DENIED);
                    return;
                }
            }
        }

        try {
            method.invoke(null, cc);
        } catch (Exception ex) {
            Logger.error("Uncaught exception during execution of \"" + CommandManager.getCommandPrefix(cc.getGuild()) + String.join(" ", getCommands()) + "\" command.");
            Logger.error(ex.getMessage());
            ex.printStackTrace();
            Logger.debug(ex);
            if (ex.getCause() instanceof CommandException) {
                cc.replyWith(ex.getMessage());
            } else {
                cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
            }
        }

        if (shouldDeleteMessages()) {
            e.getMessage().delete();
        }
    }
}
