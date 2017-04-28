package Techtony96.Discord.api.commands;

import Techtony96.Discord.api.list.ArgList;
import Techtony96.Discord.utils.ChannelUtil;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.PrivateChannel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

/**
 * Created by Tony on 2/16/2017.
 */
public class CommandContext {

    private IMessage message;
    private CustomCommand customCommand;
    private String command;
    private ArgList arguments;

    public CommandContext(CustomCommand bc, IMessage message) {
        this.message = message;
        this.customCommand = bc;
        command = getContent().substring(1, getContent().contains(" ") ? getContent().indexOf(" ") : getContent().length());
        arguments = new ArgList(getContent().split(" "));
    }

    public IMessage getMessage() {
        return message;
    }

    public IUser getUser() {
        return message.getAuthor();
    }

    public IGuild getGuild() {
        return getMessage().getGuild();
    }

    public String mentionUser() {
        return getUser().mention();
    }

    public String getUserDisplayName() {
        return getUser().getDisplayName(getGuild());
    }

    public List<IUser> getMentions() {
        return getMessage().getMentions();
    }

    public IChannel getChannel() {
        return message.getChannel();
    }

    public boolean isPrivateMessage() {
        return getChannel() instanceof PrivateChannel;
    }

    public String getContent() {
        return message.getContent();
    }

    public String getCommand() {
        return command;
    }

    public String getArgument(int index) {
        return getContent().split(" ")[index];
    }

    public int getArgCount() {
        return getContent().split(" ").length;
    }

    public ArgList getArguments() {
        return arguments;
    }

    public String combineArgs(int lowIndex, int highIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = lowIndex; i <= highIndex; i++) {
            sb.append(getArgument(i)).append(' ');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void replyWith(String message) {
        ChannelUtil.sendMessage(getChannel(), message);
    }

    public IMessage replyWith(EmbedObject embedObject) {
        return ChannelUtil.sendMessage(getChannel(), embedObject);
    }

    public void sendUsage() {
        customCommand.sendUsage(this, false);
    }
}
