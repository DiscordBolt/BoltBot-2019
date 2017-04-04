package Techtony96.Discord.modules.tempchannels;

import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import Techtony96.Discord.modules.tempchannels.exceptions.DuplicateChannelException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IUser;

public class MessageHandler {

    private IDiscordClient client;

    public MessageHandler(IDiscordClient client) {
        this.client = client;
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        /*
            !Create Command
         */
        new BotCommand(client, "create") {
            @Override
            public void execute(CommandContext cc) {
                if (cc.getMentions().size() > 0 || cc.getMessage().getChannelMentions().size() > 0){
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
        }.setArguments(2, 100).setUsage("!Create -Private [Channel Name]").setDescription("");

        /*
            !Add Command
         */
        new BotCommand(client, "add") {
            @Override
            public void execute(CommandContext cc) {
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
                    if (!(mentioned.isBot() || mentioned.equals(ch.getOwner()) || ch.getChannel().getUserOverrides().containsKey(mentioned.getID())))
                        ch.giveUserPermission(mentioned);
                }
            }
        }.setArguments(2, 100).setUsage("!Add @User1 @User2").setDescription("Give users permission to join your temporary channel.");

        /*
            !Delete Command
         */
        new BotCommand(client, "delete") {
            @Override
            public void execute(CommandContext cc) {
                if (ChannelManager.getChannel(cc.getUser()) == null) {
                    cc.replyWith(cc.getUserDisplayName() + ", you do not have a temporary voice channel.");
                    return;
                }

                ChannelManager.removeChannel(ChannelManager.getChannel(cc.getUser()));
                cc.replyWith(cc.getUserDisplayName() + ", successfully deleted your temporary channel.");
            }
        }.setArguments(1).setUsage("!Delete").setDescription("Delete your temporary voice channel.");
    }
}
