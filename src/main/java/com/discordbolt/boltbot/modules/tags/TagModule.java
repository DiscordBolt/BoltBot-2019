package com.discordbolt.boltbot.modules.tags;

import com.discordbolt.api.command.BotCommand;
import com.discordbolt.api.command.CommandContext;
import com.discordbolt.api.command.exceptions.CommandArgumentException;
import com.discordbolt.api.command.exceptions.CommandException;
import com.discordbolt.api.command.exceptions.CommandPermissionException;
import com.discordbolt.api.command.exceptions.CommandStateException;
import com.discordbolt.boltbot.api.CustomModule;
import com.discordbolt.boltbot.api.mysql.data.persistent.GuildData;
import com.discordbolt.boltbot.api.mysql.data.persistent.TagData;
import com.discordbolt.boltbot.utils.ChannelUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.modules.IModule;

import java.util.Optional;

/**
 * Created by Tony on 5/14/2017.
 */
public class TagModule extends CustomModule implements IModule {

    private static final char DEFAULT_TAG_PREFIX = '%';

    public TagModule(IDiscordClient client) {
        super(client, "Tag Module", "1.1");
    }
    
    @BotCommand(command = {"tag", "add"}, description = "Adds a Tag", usage = "Tag add [tag]", module = "Tag Module", minArgs = 4, maxArgs = 100)
    public static void tagAddCommand(CommandContext cc) throws CommandException {
        try {
            TagData tagData = TagData.create(cc.getGuild().getLongID(), cc.getAuthor().getLongID(), cc.getArgument(2), cc.combineArgs(3, cc.getArgCount() - 1));
            cc.replyWith("Successfully registered your new tag. Use `" + getTagPrefix(cc.getGuild()) + tagData.getName() + "` to view it.");
        } catch (IllegalStateException e) {
            throw new CommandStateException("That tag already exists! Please choose a different tag");
        } catch (IllegalArgumentException e) {
            throw new CommandArgumentException(e.getMessage());
        }
    }

    @BotCommand(command = {"tag", "edit"}, description = "Edits a Tag", usage = "Tag edit [tag]", module = "Tag Module", minArgs = 4, maxArgs = 100)
    public static void tagEditCommand(CommandContext cc) throws CommandException {
        Optional<TagData> tag = TagData.getById(cc.getGuild().getLongID(), cc.getArgument(2));

        if (!tag.isPresent())
            throw new CommandStateException("That tag does not exist!");
        if (tag.get().getUserId() != cc.getAuthor().getLongID())
            throw new CommandPermissionException("You do not have permission to edit this tag!");

        tag.get().setContent(cc.combineArgs(3, cc.getArgCount() - 1));
        cc.replyWith("Successfully updated your tag. Use `" + getTagPrefix(cc.getGuild()) + tag.get().getName() + "` to view it.");
    }

    @BotCommand(command = {"tag", "delete"}, description = "Deletes a Tag", usage = "Tag delete [tag]", module = "Tag Module", args = 3)
    public static void tagDeleteCommand(CommandContext cc) throws CommandException {
        Optional<TagData> tag = TagData.getById(cc.getGuild().getLongID(), cc.getArgument(2));

        if (!tag.isPresent())
            throw new CommandStateException("That tag does not exist!");
        if (tag.get().getUserId() != cc.getAuthor().getLongID())
            throw new CommandPermissionException("You do not have permission to delete this tag!");

        if (tag.get().delete()) {
            cc.replyWith("Successfully deleted your tag.");
        } else {
            cc.replyWith("An error occurred while deleting your tag, please try again later.");
        }
    }

    @BotCommand(command = {"tag", "prefix"}, description = "Set the character prefix for tags", usage = "Tag prefix [prefix]", module = "Tag Module", args = 3, permissions = Permissions.ADMINISTRATOR)
    public static void tagConfig(CommandContext cc) {
        setGuildTagPrefix(cc.getGuild(), cc.getArgument(2).charAt(0));
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
        Optional<TagData> tag = TagData.getById(e.getGuild().getLongID(), tagRequest);

        if (tag.isPresent()) {
            ChannelUtil.sendMessage(e.getChannel(), tag.get().getContent());
        } else {
            ChannelUtil.sendMessage(e.getChannel(), "That tag does not exist!");
        }
    }

    private static char getTagPrefix(IGuild guild) {
        if (guild == null || !GuildData.getById(guild.getLongID()).isPresent())
            return DEFAULT_TAG_PREFIX;
        String tagPrefix = GuildData.getById(guild.getLongID()).get().getTagPrefix();
        return tagPrefix != null ? tagPrefix.charAt(0) : DEFAULT_TAG_PREFIX;
    }

    private static void setGuildTagPrefix(IGuild guild, char prefix) {
        GuildData.getById(guild.getLongID()).get().setTagPrefix(String.valueOf(prefix));
    }
}
