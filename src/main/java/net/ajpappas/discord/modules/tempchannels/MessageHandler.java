package net.ajpappas.discord.modules.tempchannels;

import net.ajpappas.discord.api.commands.BotCommand;
import net.ajpappas.discord.api.commands.CommandContext;
import net.ajpappas.discord.modules.tempchannels.exceptions.DuplicateChannelException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

public class MessageHandler {

    private static IDiscordClient client;

    public MessageHandler(IDiscordClient client) {
        this.client = client;
    }

    @BotCommand(command = "create", module = "Temporary Channels Module", description = "Create a voice channel", usage = "Create -Private [Channel Name]", minArgs = 2, maxArgs = 100)
    public static void createCommand(CommandContext cc) {
        if (cc.getMessage().getMentions().size() > 0 || cc.getMessage().getChannelMentions().size() > 0) {
            cc.replyWith(cc.getAuthorDisplayName() + ", you should not mention any users in this command.");
            return;
        }

        boolean privateChannel = cc.getArguments().containsIgnoreCase("-Private") || cc.getArguments().containsIgnoreCase("-P");
        String channelName = cc.combineArgs(1, cc.getArgCount() - 1).replaceAll("(?i)-Private", "").replaceAll("(?i)-P", "").trim();

        if (channelName.length() < 2 || channelName.length() > 100) {
            cc.replyWith(cc.getAuthorDisplayName() + ", your channel name must be between 2 and 100 characters long.");
            return;
        }

        try {
            ChannelManager.createChannel(client, cc.getAuthor(), channelName, cc.getGuild(), privateChannel);
        } catch (DuplicateChannelException ex) {
            cc.replyWith(cc.getAuthorDisplayName() + ", you already own a temporary channel, delete it with !Delete");
        }
    }

    @BotCommand(command = "add", module = "Temporary Channels Module", description = "Give users permission to join your temporary channel.", usage = "Add @User1 @User2", minArgs = 2, maxArgs = 100)
    public static void addCommand(CommandContext cc) {
        TemporaryChannel ch = ChannelManager.getChannel(cc.getAuthor());

        if (ch == null) {
            cc.replyWith(cc.getAuthorDisplayName() + ", you do not currently have a temporary voice channel. Create one with !Create");
            return;
        }

        if (!ch.isPrivate()) {
            cc.replyWith(cc.getAuthorDisplayName() + ", your temporary voice channel isn't private.");
            return;
        }

        if (cc.getMessage().getMentions().size() < 1) {
            cc.replyWith(cc.getAuthorDisplayName() + ", no users were @Mentioned in your message.");
            return;
        }

        for (IUser mentioned : cc.getMessage().getMentions()) {
            if (!(mentioned.isBot() || mentioned.equals(ch.getOwner()) || ch.getChannel().getUserOverridesLong().containsKey(mentioned.getLongID())))
                ch.giveUserPermission(mentioned);
        }
    }

    @BotCommand(command = "delete", module = "Temporary Channels Module", description = "Delete your temporary voice channel.", usage = "Delete", args = 1)
    public static void deleteCommand(CommandContext cc) {
        if (ChannelManager.getChannel(cc.getAuthor()) == null) {
            cc.replyWith(cc.getAuthorDisplayName() + ", you do not have a temporary voice channel.");
            return;
        }

        ChannelManager.removeChannel(ChannelManager.getChannel(cc.getAuthor()));
        cc.replyWith(cc.getAuthorDisplayName() + ", successfully deleted your temporary channel.");
    }
}
