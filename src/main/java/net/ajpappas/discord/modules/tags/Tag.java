package Techtony96.Discord.modules.tags;

import Techtony96.Discord.api.commands.exceptions.CommandException;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Tony on 5/14/2017.
 */
public class Tag {

    private Long creatorID, guildID;
    private String tag, content;

    public Tag(IUser creator, IGuild guild, String tag, String content) throws CommandException {
        this.creatorID = creator.getLongID();
        this.guildID = guild.getLongID();
        this.tag = tag;
        this.content = content;
        TagFileIO.saveToFile(this);
    }

    public Long getCreatorID() {
        return creatorID;
    }

    public Long getGuildID() {
        return guildID;
    }

    public String getTag() {
        return tag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) throws CommandException {
        this.content = content;
        TagFileIO.saveToFile(this);
    }
}
