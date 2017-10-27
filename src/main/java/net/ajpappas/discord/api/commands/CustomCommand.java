package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.utils.ChannelUtil;
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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tony on 2/15/2017.
 */
public class CustomCommand {

    private String name;
    private Method execute;
    private String module;
    private String description = "";
    private String usage = "";
    private Set<String> aliases = new HashSet<>();
    private Set<String> allowedChannels = new HashSet<>();
    private EnumSet<Permissions> permissions = EnumSet.noneOf(Permissions.class);
    private int args = -1, minArgs = -1, maxArgs = -1;
    private boolean secret = false;
    private boolean allowPM = false;
    private boolean delete = false;

    public CustomCommand(Method method) {
        CommandModule.getClient().getDispatcher().registerListener(this);

        BotCommand annotation = method.getAnnotation(BotCommand.class);
        setCommand(annotation.command());
        setMethod(method);
        setModule(annotation.module());
        setDescription(annotation.description());
        setUsage(annotation.usage());
        setAliases(annotation.aliases());
        setAllowedChannels(annotation.allowedChannels());
        setPermissions(annotation.permissions());
        if (annotation.args() != -1)
            setArguments(annotation.args());
        if (annotation.minArgs() != -1 && annotation.maxArgs() != -1)
            setArguments(annotation.minArgs(), annotation.maxArgs());
        setSecret(annotation.secret());
        setAllowPM(annotation.allowPM());
        setDelete(annotation.deleteMessages());
    }

    public CustomCommand setCommand(String command) {
        this.name = command.toLowerCase();
        return this;
    }

    public CustomCommand setMethod(Method method) {
        this.execute = method;
        return this;
    }

    public CustomCommand setModule(String module) {
        this.module = module;
        return this;
    }

    public CustomCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    public CustomCommand setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public CustomCommand setAliases(Set<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public CustomCommand setAliases(String... aliases) {
        Collections.addAll(this.aliases, aliases);
        return this;
    }

    public CustomCommand setAllowedChannels(String... allowedChannels) {
        Collections.addAll(this.allowedChannels, allowedChannels);
        return this;
    }

    public CustomCommand setPermissions(EnumSet<Permissions> permissions) {
        this.permissions = permissions;
        return this;
    }

    public CustomCommand setPermissions(Permissions... permissions) {
        Collections.addAll(this.permissions, permissions);
        return this;
    }

    public CustomCommand setArguments(int arguments) {
        if (arguments < 0)
            throw new IllegalArgumentException("Arguments must be > 0");
        args = arguments;
        return this;
    }

    public CustomCommand setArguments(int minArgs, int maxArgs) {
        if (minArgs < 0 || maxArgs < 0)
            throw new IllegalArgumentException("Arguments must be > 0");
        if (maxArgs - minArgs < 0)
            throw new IllegalArgumentException("maxArgs must be larger than minArgs");
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        return this;
    }

    public CustomCommand setSecret(boolean secret) {
        this.secret = secret;
        return this;
    }

    public CustomCommand setAllowPM(boolean allowPM) {
        this.allowPM = allowPM;
        return this;
    }

    public CustomCommand setDelete(boolean deleteMessages) {
        this.delete = deleteMessages;
        return this;
    }

    public String getName() {
        return name;
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

    public Set<String> getAliases() {
        return aliases;
    }

    public Set<String> getAllowedChannels() {
        return allowedChannels;
    }

    public EnumSet<Permissions> getPermissions() {
        return permissions;
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

    public void sendUsage(CommandContext cc, boolean mentionUser) {
        ChannelUtil.sendMessage(cc.getChannel(), (mentionUser ? cc.mentionUser() : "") + " " + getUsage(cc.getGuild()));
    }

    @EventSubscriber
    public void onMesageEvent(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        IUser user = e.getAuthor();

        if (user.isBot()){ // Ignore bots
            return;
        }

        // Do not respond to PMs right now
        if (!isAllowPM() && e.getChannel() instanceof PrivateChannel) {
            return;
        }

        if (message.length() <= 1) // Message is just a single prefix.
            return;

        // Check if message typed was a command
        if (!message.startsWith(CommandManager.getCommandPrefix(e.getGuild())))
            return;

        // Check if command was this command
        CommandContext cc = new CommandContext(this, e.getMessage());

        if (!(cc.getCommand().equalsIgnoreCase(getName()) || (getAliases().size() > 0 && getAliases().stream().filter(a -> a.equalsIgnoreCase(cc.getCommand())).findAny().isPresent())))
            return;

        if (!(cc.isPrivateMessage() && isAllowPM()) && getAllowedChannels().size() > 0 && !getAllowedChannels().contains(cc.getChannel().getName())) {
            cc.replyWith(cc.getCommand() + " can not be executed in " + cc.getChannel().mention());
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
        if (e.getChannel() instanceof PrivateChannel && getPermissions().size() != 0) {
            cc.replyWith("This command requires permissions which can not be checked in a PM. Please execute this command in a guild.");
            return;
        }

        if (UserUtil.isBotOwner(cc.getUser())) {
            // Do not check permissions
        } else {
            for (Permissions p : getPermissions()) {
                if (!user.getPermissionsForGuild(e.getGuild()).contains(p)) {
                    cc.replyWith(cc.mentionUser() + " " + ExceptionMessage.PERMISSION_DENIED);
                    return;
                }
            }
        }

        try {
            execute.invoke(null, cc);
        } catch (Exception ex) {
            Logger.error("Internal error occurred while running " + getName() + " command.");
            Logger.error(ex.getMessage());
            ex.printStackTrace();
            Logger.debug(ex);
            if (ex.getCause() instanceof CommandException) {
                cc.replyWith(ex.getMessage());
            } else {
                cc.replyWith(ExceptionMessage.COMMAND_PROCESS_EXCEPTION);
            }

            return;
        }

        if (shouldDeleteMessages()) {
            e.getMessage().delete();
        }
    }
}
