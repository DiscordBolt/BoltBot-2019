package Techtony96.Discord.modules.tags;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.api.commands.exceptions.CommandException;
import Techtony96.Discord.utils.ChannelUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.modules.IModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tony on 5/14/2017.
 */
public class TagModule extends CustomModule implements IModule {

    private static final char TAG_PREFIX = '%';

    protected static List<Tag> tags = new ArrayList<>();

    public TagModule() {
        super("Tag Module", "1.0");
        TagFileIO.loadTags();
    }

    @BotCommand(command = "tag", description = "Add/Edit/Delete Tags", usage = "Tag [add/edit/delete] [tag]", module = "Tag Module")
    public static void tagCommand(CommandContext cc) {
        if (cc.getArgCount() < 2) {
            cc.sendUsage();
            return;
        }

        String instruction = cc.getArgument(1).toLowerCase();

        if (instruction.equals("add")) { //!Tag Add [tag] [content]
            if (cc.getArgCount() < 4) {
                cc.replyWith("Incorrect number of arguments. Usage: !Tag Add [tag] [content]");
                return;
            }

            String tag = cc.getArgument(2);

            if (findTag(cc.getGuild(), tag).isPresent()) {
                cc.replyWith("That tag already exists! Please choose a different tag");
                return;
            }

            try {
                tags.add(new Tag(cc.getUser(), cc.getGuild(), tag, cc.combineArgs(3, cc.getArgCount() - 1)));
                cc.replyWith("Successfully registered your new tag. Use `" + TAG_PREFIX + tag + "` to view it.");
                return;
            } catch (CommandException e) {
                cc.replyWith(e.getMessage());
                return;
            }
        } else if (instruction.equals("edit")) { //!Tag Edit [tag] [newContent]

        } else if (instruction.equals("delete")) { // !Tag Delete [tag]

        } else {
            cc.sendUsage();
            return;
        }
    }

    @EventSubscriber
    public void onTagRequest(MessageReceivedEvent e) {
        if (e.getMessage().getContent().charAt(0) != TAG_PREFIX)
            return;

        String tagRequest = e.getMessage().getContent().replaceFirst(TAG_PREFIX + "", "").split(" ", 2)[0];
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
}
