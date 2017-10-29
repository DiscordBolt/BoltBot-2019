package net.ajpappas.discord.api.commands;

import net.ajpappas.discord.api.list.ArgList;
import net.ajpappas.discord.utils.ChannelUtil;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.PrivateChannel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Tony on 2/16/2017.
 */
public class CommandContext {

    private IMessage message;
    private ArgList arguments;
    private CustomCommand customCommand;

    public CommandContext(IMessage message, CustomCommand customCommand) {
        this.message = message;
        arguments = new ArgList(getMessageContent().substring(1, getMessageContent().length()).split(" "));
        this.customCommand = customCommand;
    }

    public IMessage getMessage() {
        return message;
    }

    public IUser getAuthor() {
        return message.getAuthor();
    }

    public String getAuthorDisplayName() {
        return getAuthor().getDisplayName(getGuild());
    }

    public IGuild getGuild() {
        return getMessage().getGuild();
    }

    public IChannel getChannel() {
        return message.getChannel();
    }

    public boolean isPrivateMessage() {
        return getChannel() instanceof PrivateChannel;
    }

    public String getMessageContent() {
        return message.getContent();
    }

    public String getUserBaseCommand() {
        return arguments.get(0);
    }

    public ArgList getArguments() {
        return arguments;
    }

    public String getArgument(int index) {
        return arguments.get(index);
    }

    public int getArgCount() {
        return getArguments().size();
    }

    public String combineArgs(int lowIndex, int highIndex) {
        StringBuilder sb = new StringBuilder();
        sb.append(getArgument(lowIndex));
        for (int i = lowIndex + 1; i <= highIndex; i++)
            sb.append(' ').append(getArgument(i));

        return sb.toString();
    }

    public IMessage replyWith(String message) {
        return ChannelUtil.sendMessage(getChannel(), message);
    }

    public IMessage replyWith(EmbedObject embedObject) {
        return ChannelUtil.sendMessage(getChannel(), embedObject);
    }

    public void sendUsage() {
        replyWith(customCommand.getUsage(getGuild()));
    }
}
