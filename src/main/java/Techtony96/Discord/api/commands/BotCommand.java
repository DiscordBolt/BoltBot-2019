package Techtony96.Discord.api.commands;

import Techtony96.Discord.utils.ChannelUtil;
import Techtony96.Discord.utils.ExceptionMessage;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.PrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tony on 2/15/2017.
 */
public abstract class BotCommand {

    private final String COMMAND_PREFIX = "!";

    private final String name;
    private String description = "";
    private String usage = "";
    private Set<String> aliases = new HashSet<>();
    private EnumSet<Permissions> permissionss = EnumSet.noneOf(Permissions.class);
    private int args = -1, minArgs = -1, maxArgs = -1;
    private boolean secret = false;

    /**
     * Create a new Bot Command
     * @param command name of the command
     */
    public BotCommand(IDiscordClient client, String command){
        this.name = command;
        client.getDispatcher().registerListener(this);
        CommandManager.register(this);
    }

    public BotCommand setDescription(String description){
        this.description = description;
        return this;
    }

    public BotCommand setUsage(String usage){
        this.usage = usage;
        return this;
    }

    public BotCommand setAliases(Set<String> aliases){
        this.aliases = aliases;
        return this;
    }

    public BotCommand setAliases(String... aliases){
        Collections.addAll(this.aliases, aliases);
        return this;
    }

    public BotCommand setPermissions(EnumSet<Permissions> permissions){
        this.permissionss = permissions;
        return this;
    }

    public BotCommand setPermissions(Permissions... permissions){
        Collections.addAll(this.permissionss, permissions);
        return this;
    }

    public BotCommand setArguments(int arguments){
        if (arguments < 0)
            throw new IllegalArgumentException("Arguments must be > 0");
        args = arguments;
        return this;
    }

    public BotCommand setArguments(int minArgs, int maxArgs){
        if (minArgs < 0 || maxArgs < 0)
            throw new IllegalArgumentException("Arguments must be > 0");
        if (maxArgs - minArgs < 0)
            throw new IllegalArgumentException("maxArgs must be larger than minArgs");
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        return this;
    }

    public BotCommand setSecret(boolean secret){
        this.secret = secret;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public EnumSet<Permissions> getPermissionss() {
        return permissionss;
    }

    public boolean isSecret(){
        return secret;
    }

    public abstract void execute(CommandContext cc);

    public void sendUsage(CommandContext cc, boolean mentionUser){
        ChannelUtil.sendMessage(cc.getChannel(), (mentionUser ? cc.mentionUser() : "" ) + " " + getUsage());
    }

    @EventSubscriber
    public void OnMesageEvent(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        IUser user = e.getAuthor();

        // Do not respond to PMs right now
        if (e.getChannel() instanceof PrivateChannel){
            return;
        }

        // Check if message typed was a command
        if (!message.startsWith(COMMAND_PREFIX))
            return;

        // Check if command was this command
        CommandContext cc = new CommandContext(e.getMessage());
        if (!cc.getCommand().equalsIgnoreCase(getName()) && !aliases.stream().filter(a -> a.equalsIgnoreCase(cc.getCommand())).findAny().isPresent())
            return;

        // Argument count check
        if (args != -1){
            if (cc.getArgCount() != args) {
                cc.replyWith("Incorrect Argument Count." + (getUsage().length() > 0 ? " Usage: " + getUsage() : ""));
                return;
            }
        }

        if (minArgs != -1 && maxArgs != -1){
            if (cc.getArgCount() < minArgs || cc.getArgCount() > maxArgs){
                cc.replyWith("Incorrect Argument Count." + (getUsage().length() > 0 ? " Usage: " + getUsage() : ""));
                return;
            }
        }

        // Permission check
        for (Permissions p : getPermissionss()){
            if (!user.getPermissionsForGuild(e.getGuild()).contains(p)){
                cc.replyWith(cc.mentionUser() + " " + ExceptionMessage.PERMISSION_DENIED);
                return;
            }
        }

        execute(cc);
    }
}
