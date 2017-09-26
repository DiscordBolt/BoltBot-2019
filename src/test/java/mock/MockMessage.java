package mock;

import com.vdurmont.emoji.Emoji;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageTokenizer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Techt on 5/2/2017.
 */
public class MockMessage implements IMessage {

    private long id;
    private String content;
    private IUser author;
    private IChannel channel;

    public MockMessage(String content, long id, IUser author, IChannel channel) {
        this.content = content;
        this.id = id;
        this.author = author;
        this.channel = channel;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public IChannel getChannel() {
        return channel;
    }

    @Override
    public IUser getAuthor() {
        return author;
    }

    @Override
    public long getLongID() {
        return id;
    }

    @Override
    public IGuild getGuild() {
        return getChannel().getGuild();
    }

    @Deprecated
    @Override
    public LocalDateTime getTimestamp() {
        return null;
    }

    @Deprecated
    @Override
    public List<IUser> getMentions() {
        return null;
    }

    @Deprecated
    @Override
    public List<IRole> getRoleMentions() {
        return null;
    }

    @Deprecated
    @Override
    public List<IChannel> getChannelMentions() {
        return null;
    }

    @Deprecated
    @Override
    public List<Attachment> getAttachments() {
        return null;
    }

    @Deprecated
    @Override
    public List<IEmbed> getEmbeds() {
        return null;
    }

    @Deprecated
    @Override
    public IMessage reply(String content) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage reply(String content, EmbedObject embed) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage edit(String content) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage edit(String content, EmbedObject embed) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage edit(EmbedObject embed) {
        return null;
    }

    @Deprecated
    @Override
    public boolean mentionsEveryone() {
        return false;
    }

    @Deprecated
    @Override
    public boolean mentionsHere() {
        return false;
    }

    @Deprecated
    @Override
    public void delete() {

    }

    @Deprecated
    @Override
    public Optional<LocalDateTime> getEditedTimestamp() {
        return null;
    }

    @Deprecated
    @Override
    public boolean isPinned() {
        return false;
    }

    @Deprecated
    @Override
    public String getFormattedContent() {
        return null;
    }

    @Deprecated
    @Override
    public List<IReaction> getReactions() {
        return null;
    }

    @Deprecated
    @Override
    public IReaction getReactionByIEmoji(IEmoji emoji) {
        return null;
    }

    @Override
    public IReaction getReactionByEmoji(IEmoji iEmoji) {
        return null;
    }

    @Override
    public IReaction getReactionByID(long l) {
        return null;
    }

    @Deprecated
    @Override
    public IReaction getReactionByUnicode(String name) {
        return null;
    }

    @Override
    public IReaction getReactionByEmoji(ReactionEmoji reactionEmoji) {
        return null;
    }

    @Deprecated
    @Override
    public IReaction getReactionByUnicode(Emoji emoji) {
        return null;
    }

    @Deprecated
    @Override
    public void removeAllReactions() {

    }

    @Deprecated
    @Override
    public void addReaction(IReaction reaction) {

    }

    @Deprecated
    @Override
    public void addReaction(IEmoji emoji) {

    }

    @Deprecated
    @Override
    public void addReaction(String emoji) {

    }

    @Override
    public void addReaction(ReactionEmoji reactionEmoji) {

    }

    @Deprecated
    @Override
    public void addReaction(Emoji emoji) {

    }

    @Deprecated
    @Override
    public void removeReaction(IUser user, IReaction reaction) {

    }

    @Override
    public void removeReaction(IUser iUser, ReactionEmoji reactionEmoji) {

    }

    @Override
    public void removeReaction(IUser iUser, IEmoji iEmoji) {

    }

    @Override
    public void removeReaction(IUser iUser, Emoji emoji) {

    }

    @Override
    public void removeReaction(IUser iUser, String s) {

    }

    @Deprecated
    @Override
    public void removeReaction(IReaction reaction) {

    }

    @Deprecated
    @Override
    public MessageTokenizer tokenize() {
        return null;
    }

    @Deprecated
    @Override
    public boolean isDeleted() {
        return false;
    }

    @Deprecated
    @Override
    public long getWebhookLongID() {
        return 0;
    }

    @Deprecated
    @Override
    public IDiscordClient getClient() {
        return null;
    }

    @Deprecated
    @Override
    public IShard getShard() {
        return null;
    }

    @Deprecated
    @Override
    public IMessage copy() {
        return null;
    }
}
