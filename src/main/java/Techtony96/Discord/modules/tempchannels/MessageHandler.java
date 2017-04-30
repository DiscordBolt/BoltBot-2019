package Techtony96.Discord.modules.tempchannels;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.modules.tempchannels.exceptions.DuplicateChannelException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

public class MessageHandler {

    private static IDiscordClient client;

    public MessageHandler(IDiscordClient client) {
        this.client = client;
    }

    @BotCommand(command = "create", module = "Temporary Channels Module", description = "Create a voice channel", usage = "!Create -Private [Channel Name]", minArgs = 2, maxArgs = 100)
    public static void createCommand(CommandContext cc) {
        if (cc.getMentions().size() > 0 || cc.getMessage().getChannelMentions().size() > 0) {
            cc.replyWith(cc.getUserDisplayName() + ", you should not mention any users in this command.");
            return;
        }

        boolean privateChannel = cc.getArguments().containsIgnoreCase("-Private") || cc.getArguments().containsIgnoreCase("-P");
        String channelName = cc.combineArgs(1, cc.getArgCount() - 1).replaceAll("(?i)-Private", "").replaceAll("(?i)-P", "").trim();

        if (channelName.length() < 2 || channelName.length() > 100) {
            cc.replyWith(cc.getUserDisplayName() + ", your channel name must be between 2 and 100 characters long.");
            return;
        }

        try {
            ChannelManager.createChannel(client, cc.getUser(), channelName, cc.getGuild(), privateChannel);
        } catch (DuplicateChannelException ex) {
            cc.replyWith(cc.getUserDisplayName() + ", you already own a temporary channel, delete it with !Delete");
        }
    }

    @BotCommand(command = "add", module = "Temporary Channels Module", description = "Give users permission to join your temporary channel.", usage = "!Add @User1 @User2", minArgs = 2, maxArgs = 100)
    public static void addCommand(CommandContext cc) {
        TemporaryChannel ch = ChannelManager.getChannel(cc.getUser());

        if (ch == null) {
            cc.replyWith(cc.getUserDisplayName() + ", you do not currently have a temporary voice channel. Create one with !Create");
            return;
        }

        if (!ch.isPrivate()) {
            cc.replyWith(cc.getUserDisplayName() + ", your temporary voice channel isn't private.");
            return;
        }

        if (cc.getMentions().size() < 1) {
            cc.replyWith(cc.getUserDisplayName() + ", no users were @Mentioned in your message.");
            return;
        }

        for (IUser mentioned : cc.getMentions()) {
            if (!(mentioned.isBot() || mentioned.equals(ch.getOwner()) || ch.getChannel().getUserOverridesLong().containsKey(mentioned.getLongID())))
                ch.giveUserPermission(mentioned);
        }
    }

    @BotCommand(command = "delete", module = "Temporary Channels Module", description = "Delete your temporary voice channel.", usage = "!Delete", args = 1)
    public static void deleteCommand(CommandContext cc) {
        if (ChannelManager.getChannel(cc.getUser()) == null) {
            cc.replyWith(cc.getUserDisplayName() + ", you do not have a temporary voice channel.");
            return;
        }

        ChannelManager.removeChannel(ChannelManager.getChannel(cc.getUser()));
        cc.replyWith(cc.getUserDisplayName() + ", successfully deleted your temporary channel.");
    }
}
