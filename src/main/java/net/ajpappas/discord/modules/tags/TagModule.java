package net.ajpappas.discord.modules.tags;

import com.google.gson.reflect.TypeToken;
import net.ajpappas.discord.api.CustomModule;
import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.api.commands.exceptions.CommandPermissionException;
import net.ajpappas.discord.api.commands.exceptions.CommandStateException;
import net.ajpappas.discord.utils.ChannelUtil;
import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.modules.IModule;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Tony on 5/14/2017.
 */
public class TagModule extends CustomModule implements IModule {

    private static final char DEFAULT_TAG_PREFIX = '%';
    private static HashMap<Long, Character> tagPrefixes = new HashMap<>();
    private static final File prefixFile = Paths.get(System.getProperty("user.dir"), "TagPrefixes.json").toFile();

    protected static List<Tag> tags = new ArrayList<>();

    public TagModule(IDiscordClient client) {
        super(client, "Tag Module", "1.0");
        TagFileIO.loadTags();

        try {
            if (!prefixFile.exists()){
                Files.write(prefixFile.toPath(), Collections.singletonList("{}"), Charset.forName("UTF-8"));
            }
        } catch (IOException e){
            Logger.error("Unable to create tag prefix file.");
            Logger.debug(e);
        }

        try {
            tagPrefixes = TagFileIO.gson.fromJson(new FileReader(prefixFile), new TypeToken<Map<Long, Character>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            Logger.error("Tag Prefix file does not exist!");
            Logger.debug(e);
        }
    }
    
    @BotCommand(command = {"tag", "add"}, description = "Adds a Tag", usage = "Tag add [tag]", module = "Tag Module", minArgs = 4, maxArgs = 100)
    public static void tagAddCommand(CommandContext cc) throws CommandException {
        String tag = cc.getArgument(2);

        if (findTag(cc.getGuild(), tag).isPresent())
            throw new CommandStateException("That tag already exists! Please choose a different tag");

        tags.add(new Tag(cc.getAuthor(), cc.getGuild(), tag, cc.combineArgs(3, cc.getArgCount() - 1)));
        cc.replyWith("Successfully registered your new tag. Use `" + getTagPrefix(cc.getGuild()) + tag + "` to view it.");
    }

    @BotCommand(command = {"tag", "edit"}, description = "Edits a Tag", usage = "Tag edit [tag]", module = "Tag Module", minArgs = 4, maxArgs = 100)
    public static void tagEditCommand(CommandContext cc) throws CommandException {
        Optional<Tag> tag = findTag(cc.getGuild(), cc.getArgument(2));

        if (!tag.isPresent())
            throw new CommandStateException("That tag does not exist!");
        if (!tag.get().getCreatorID().equals(cc.getAuthor().getLongID()))
            throw new CommandPermissionException("You do not have permission to edit this tag!");

        // Delete the old tag
        TagFileIO.deleteFile(tag.get());
        tags.remove(tag.get());

        tags.add(new Tag(cc.getAuthor(), cc.getGuild(), tag.get().getTag(), cc.combineArgs(3, cc.getArgCount() - 1)));
        cc.replyWith("Successfully updated your tag. Use `" + getTagPrefix(cc.getGuild()) + tag.get().getTag() + "` to view it.");
    }

    @BotCommand(command = {"tag", "delete"}, description = "Deletes a Tag", usage = "Tag delete [tag]", module = "Tag Module", args = 3)
    public static void tagDeleteCommand(CommandContext cc) throws CommandException {
        Optional<Tag> tag = findTag(cc.getGuild(), cc.getArgument(2));

        if (!tag.isPresent())
            throw new CommandStateException("That tag does not exist!");
        if (!tag.get().getCreatorID().equals(cc.getAuthor().getLongID()))
            throw new CommandPermissionException("You do not have permission to delete this tag!");

        // Delete the old tag
        TagFileIO.deleteFile(tag.get());
        tags.remove(tag.get());
        cc.replyWith("Successfully deleted your tag.");
    }

    @BotCommand(command = "TagPrefix", description = "Set prefix options for Tag Module", usage = "TagPrefix [prefix]", module = "Tag Module", permissions = Permissions.ADMINISTRATOR)
    public static void tagConfig(CommandContext cc) {
        if (cc.getArgCount() < 2 || cc.getArgCount() > 2 || cc.getArgument(1).length() > 1) {
            cc.sendUsage();
            return;
        }

        char newPrefix = cc.getArgument(1).charAt(0);
        setGuildTagPrefix(cc.getGuild(), newPrefix);
        cc.replyWith("Set your tag prefix to `" + getTagPrefix(cc.getGuild()) + "`");
    }

    @EventSubscriber
    public void onTagRequest(MessageReceivedEvent e) {
        if (e.getGuild() == null)
            return;
        if (e.getMessage().getContent() == null || e.getMessage().getContent().length() <= 0)
            return;
        if (e.getMessage().getContent().charAt(0) != getTagPrefix(e.getGuild()))
            return;

        // String tagRequest = e.getMessage().getContent().replaceFirst(getTagPrefix(e.getGuild()) + "", "").split(" ", 2)[0];
        String tagRequest = e.getMessage().getContent().substring(1, e.getMessage().getContent().length()).split(" ", 2)[0];
        Optional<Tag> tag = findTag(e.getGuild(), tagRequest);

        if (tag.isPresent()) {
            ChannelUtil.sendMessage(e.getChannel(), tag.get().getContent());
            return;
        } else {
            ChannelUtil.sendMessage(e.getChannel(), "That tag does not exist!");
            return;
        }
    }

    private static Optional<Tag> findTag(IGuild guild, String tag) {
        return tags.stream().filter(t -> t.getGuildID().equals(guild.getLongID()) && t.getTag().equalsIgnoreCase(tag)).findAny();
    }

    private static char getTagPrefix(IGuild guild) {
        if (guild == null || tagPrefixes == null)
            return DEFAULT_TAG_PREFIX;
        return tagPrefixes.getOrDefault(guild.getLongID(), DEFAULT_TAG_PREFIX);
    }

    private static void setGuildTagPrefix(IGuild guild, char prefix) {
        tagPrefixes.put(guild.getLongID(), prefix);
        writePrefixes();
    }

    private static void writePrefixes() {
        try {
            prefixFile.createNewFile();
            FileWriter fw = new FileWriter(prefixFile);
            fw.write(TagFileIO.gson.toJson(tagPrefixes));
            fw.close();
        } catch (IOException e) {
            Logger.error("Unable to save tag prefix file.");
            Logger.debug(e);
        }
    }
}
