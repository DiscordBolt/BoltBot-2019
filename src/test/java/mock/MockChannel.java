package mock;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;
import sx.blah.discord.util.cache.LongMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Techt on 5/2/2017.
 */
public class MockChannel implements IChannel {

    private String name;
    private long id;
    private IGuild guild;

    public MockChannel(String name, long id, IGuild guild) {
        this.name = name;
        this.id = id;
        this.guild = guild;
    }

    @Override
    public long getLongID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IGuild getGuild() {
        return guild;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistory() {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistory(int messageCount) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryFrom(LocalDateTime startDate) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryFrom(LocalDateTime startDate, int maxMessageCount) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryTo(LocalDateTime endDate) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryTo(LocalDateTime endDate, int maxMessageCount) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryIn(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryIn(LocalDateTime startDate, LocalDateTime endDate, int maxMessageCount) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryFrom(long id) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryFrom(long id, int maxMessageCount) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryTo(long id) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryTo(long id, int maxMessageCount) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryIn(long beginID, long endID) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getMessageHistoryIn(long beginID, long endID, int maxMessageCount) {
        return null;
    }

    @Deprecated
    @Override
    public MessageHistory getFullMessageHistory() {
        return null;
    }

    @Deprecated
    @Override
    public List<IMessage> bulkDelete() {
        return null;
    }

    @Deprecated
    @Override
    public List<IMessage> bulkDelete(List<IMessage> messages) {
        return null;
    }

    @Deprecated
    @Override
    public int getMaxInternalCacheCount() {
        return 0;
    }

    @Deprecated
    @Override
    public int getInternalCacheCount() {
        return 0;
    }

    @Deprecated
    @Override
    public IMessage getMessageByID(long messageID) {
        return null;
    }

    @Deprecated
    @Override
    public boolean isPrivate() {
        return false;
    }

    @Deprecated
    @Override
    public boolean isNSFW() {
        return false;
    }

    @Deprecated
    @Override
    public String getTopic() {
        return null;
    }

    @Deprecated
    @Override
    public String mention() {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendMessage(String content) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendMessage(EmbedObject embed) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendMessage(String content, boolean tts) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendMessage(String content, EmbedObject embed) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendMessage(String content, EmbedObject embed, boolean tts) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(File file) throws FileNotFoundException {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFiles(File... files) throws FileNotFoundException {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(String content, File file) throws FileNotFoundException {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFiles(String content, File... files) throws FileNotFoundException {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(EmbedObject embed, File file) throws FileNotFoundException {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFiles(EmbedObject embed, File... files) throws FileNotFoundException {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(String content, InputStream file, String fileName) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFiles(String content, AttachmentPartEntry... entries) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(EmbedObject embed, InputStream file, String fileName) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFiles(EmbedObject embed, AttachmentPartEntry... entries) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(String content, boolean tts, InputStream file, String fileName) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFiles(String content, boolean tts, AttachmentPartEntry... entries) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(String content, boolean tts, InputStream file, String fileName, EmbedObject embed) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFiles(String content, boolean tts, EmbedObject embed, AttachmentPartEntry... entries) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage sendFile(MessageBuilder builder, InputStream file, String fileName) {
        return null;
    }

    @Deprecated
    @Override
    public IInvite createInvite(int maxAge, int maxUses, boolean temporary, boolean unique) {
        return null;
    }

    @Deprecated
    @Override
    public void toggleTypingStatus() {

    }

    @Deprecated
    @Override
    public void setTypingStatus(boolean typing) {

    }

    @Deprecated
    @Override
    public boolean getTypingStatus() {
        return false;
    }

    @Deprecated
    @Override
    public void edit(String name, int position, String topic) {

    }

    @Deprecated
    @Override
    public void changeName(String name) {

    }

    @Deprecated
    @Override
    public void changePosition(int position) {

    }

    @Deprecated
    @Override
    public void changeTopic(String topic) {

    }

    @Override
    public void changeNSFW(boolean b) {

    }

    @Deprecated
    @Override
    public int getPosition() {
        return 0;
    }

    @Deprecated
    @Override
    public void delete() {

    }

    @Deprecated
    @Override
    public LongMap<PermissionOverride> getUserOverridesLong() {
        return null;
    }

    @Deprecated
    @Override
    public LongMap<PermissionOverride> getRoleOverridesLong() {
        return null;
    }

    @Deprecated
    @Override
    public EnumSet<Permissions> getModifiedPermissions(IUser user) {
        return null;
    }

    @Deprecated
    @Override
    public EnumSet<Permissions> getModifiedPermissions(IRole role) {
        return null;
    }

    @Deprecated
    @Override
    public void removePermissionsOverride(IUser user) {

    }

    @Deprecated
    @Override
    public void removePermissionsOverride(IRole role) {

    }

    @Deprecated
    @Override
    public void overrideRolePermissions(IRole role, EnumSet<Permissions> toAdd, EnumSet<Permissions> toRemove) {

    }

    @Deprecated
    @Override
    public void overrideUserPermissions(IUser user, EnumSet<Permissions> toAdd, EnumSet<Permissions> toRemove) {

    }

    @Deprecated
    @Override
    public List<IExtendedInvite> getExtendedInvites() {
        return null;
    }

    @Deprecated
    @Override
    public List<IUser> getUsersHere() {
        return null;
    }

    @Deprecated
    @Override
    public List<IMessage> getPinnedMessages() {
        return null;
    }

    @Deprecated
    @Override
    public void pin(IMessage message) {

    }

    @Deprecated
    @Override
    public void unpin(IMessage message) {

    }

    @Deprecated
    @Override
    public List<IWebhook> getWebhooks() {
        return null;
    }

    @Deprecated
    @Override
    public IWebhook getWebhookByID(long id) {
        return null;
    }

    @Deprecated
    @Override
    public List<IWebhook> getWebhooksByName(String name) {
        return null;
    }

    @Deprecated
    @Override
    public IWebhook createWebhook(String name) {
        return null;
    }

    @Deprecated
    @Override
    public IWebhook createWebhook(String name, Image avatar) {
        return null;
    }

    @Deprecated
    @Override
    public IWebhook createWebhook(String name, String avatar) {
        return null;
    }

    @Deprecated
    @Override
    public boolean isDeleted() {
        return false;
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
    public IChannel copy() {
        return null;
    }
}
