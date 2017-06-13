package mock;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.Ban;
import sx.blah.discord.util.Image;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Techt on 5/2/2017.
 */
public class MockGuild implements IGuild {

    private String name;
    private long id, ownerID;

    public MockGuild(String name, long id, long ownerID) {
        this.name = name;
        this.id = id;
        this.ownerID = ownerID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getLongID() {
        return id;
    }

    @Override
    public long getOwnerLongID() {
        return ownerID;
    }

    @Deprecated
    @Override
    public IUser getOwner() {
        return null;
    }

    @Deprecated
    @Override
    public String getIcon() {
        return null;
    }

    @Deprecated
    @Override
    public String getIconURL() {
        return null;
    }

    @Deprecated
    @Override
    public List<IChannel> getChannels() {
        return null;
    }

    @Deprecated
    @Override
    public IChannel getChannelByID(long id) {
        return null;
    }

    @Deprecated
    @Override
    public List<IUser> getUsers() {
        return null;
    }

    @Deprecated
    @Override
    public IUser getUserByID(long id) {
        return null;
    }

    @Deprecated
    @Override
    public List<IChannel> getChannelsByName(String name) {
        return null;
    }

    @Deprecated
    @Override
    public List<IVoiceChannel> getVoiceChannelsByName(String name) {
        return null;
    }

    @Deprecated
    @Override
    public List<IUser> getUsersByName(String name) {
        return null;
    }

    @Deprecated
    @Override
    public List<IUser> getUsersByName(String name, boolean includeNicknames) {
        return null;
    }

    @Deprecated
    @Override
    public List<IUser> getUsersByRole(IRole role) {
        return null;
    }

    @Deprecated
    @Override
    public List<IRole> getRoles() {
        return null;
    }

    @Deprecated
    @Override
    public List<IRole> getRolesForUser(IUser user) {
        return null;
    }

    @Deprecated
    @Override
    public IRole getRoleByID(long id) {
        return null;
    }

    @Deprecated
    @Override
    public List<IRole> getRolesByName(String name) {
        return null;
    }

    @Deprecated
    @Override
    public List<IVoiceChannel> getVoiceChannels() {
        return null;
    }

    @Deprecated
    @Override
    public IVoiceChannel getVoiceChannelByID(long id) {
        return null;
    }

    @Deprecated
    @Override
    public IVoiceChannel getConnectedVoiceChannel() {
        return null;
    }

    @Deprecated
    @Override
    public IVoiceChannel getAFKChannel() {
        return null;
    }

    @Deprecated
    @Override
    public int getAFKTimeout() {
        return 0;
    }

    @Deprecated
    @Override
    public IRole createRole() {
        return null;
    }

    @Deprecated
    @Override
    public List<IUser> getBannedUsers() {
        return null;
    }

    @Deprecated
    @Override
    public List<Ban> getBans() {
        return null;
    }

    @Deprecated
    @Override
    public void banUser(IUser user) {

    }

    @Deprecated
    @Override
    public void banUser(IUser user, int deleteMessagesForDays) {

    }

    @Deprecated
    @Override
    public void banUser(IUser iUser, String s) {

    }

    @Deprecated
    @Override
    public void banUser(IUser iUser, String s, int i) {

    }

    @Deprecated
    @Override
    public void banUser(long userID) {

    }

    @Deprecated
    @Override
    public void banUser(long userID, int deleteMessagesForDays) {

    }

    @Override
    public void banUser(long l, String s) {

    }

    @Deprecated
    @Override
    public void banUser(long l, String s, int i) {

    }

    @Deprecated
    @Override
    public void pardonUser(long userID) {

    }

    @Deprecated
    @Override
    public void kickUser(IUser user) {

    }

    @Deprecated
    @Override
    public void kickUser(IUser iUser, String s) {

    }

    @Deprecated
    @Override
    public void editUserRoles(IUser user, IRole[] roles) {

    }

    @Deprecated
    @Override
    public void setDeafenUser(IUser user, boolean deafen) {

    }

    @Deprecated
    @Override
    public void setMuteUser(IUser user, boolean mute) {

    }

    @Deprecated
    @Override
    public void setUserNickname(IUser user, String nick) {

    }

    @Deprecated
    @Override
    public void edit(String name, IRegion region, VerificationLevel level, Image icon, IVoiceChannel afkChannel, int afkTimeout) {

    }

    @Deprecated
    @Override
    public void changeName(String name) {

    }

    @Deprecated
    @Override
    public void changeRegion(IRegion region) {

    }

    @Deprecated
    @Override
    public void changeVerificationLevel(VerificationLevel verification) {

    }

    @Deprecated
    @Override
    public void changeIcon(Image icon) {

    }

    @Deprecated
    @Override
    public void changeAFKChannel(IVoiceChannel channel) {

    }

    @Deprecated
    @Override
    public void changeAFKTimeout(int timeout) {

    }

    @Deprecated
    @Override
    public void deleteGuild() {

    }

    @Deprecated
    @Override
    public void leaveGuild() {

    }

    @Deprecated
    @Override
    public void leave() {

    }

    @Deprecated
    @Override
    public IChannel createChannel(String name) {
        return null;
    }

    @Deprecated
    @Override
    public IVoiceChannel createVoiceChannel(String name) {
        return null;
    }

    @Deprecated
    @Override
    public IRegion getRegion() {
        return null;
    }

    @Deprecated
    @Override
    public VerificationLevel getVerificationLevel() {
        return null;
    }

    @Deprecated
    @Override
    public IRole getEveryoneRole() {
        return null;
    }

    @Deprecated
    @Override
    public IChannel getGeneralChannel() {
        return null;
    }

    @Deprecated
    @Override
    public List<IInvite> getInvites() {
        return null;
    }

    @Deprecated
    @Override
    public List<IExtendedInvite> getExtendedInvites() {
        return null;
    }

    @Deprecated
    @Override
    public void reorderRoles(IRole... rolesInOrder) {

    }

    @Deprecated
    @Override
    public int getUsersToBePruned(int days) {
        return 0;
    }

    @Deprecated
    @Override
    public int pruneUsers(int days) {
        return 0;
    }

    @Deprecated
    @Override
    public boolean isDeleted() {
        return false;
    }

    @Deprecated
    @Override
    public IAudioManager getAudioManager() {
        return null;
    }

    @Deprecated
    @Override
    public LocalDateTime getJoinTimeForUser(IUser user) {
        return null;
    }

    @Deprecated
    @Override
    public IMessage getMessageByID(long id) {
        return null;
    }

    @Deprecated
    @Override
    public List<IEmoji> getEmojis() {
        return null;
    }

    @Deprecated
    @Override
    public IEmoji getEmojiByID(long id) {
        return null;
    }

    @Deprecated
    @Override
    public IEmoji getEmojiByName(String name) {
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
    public List<IWebhook> getWebhooks() {
        return null;
    }

    @Deprecated
    @Override
    public int getTotalMemberCount() {
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
    public IGuild copy() {
        return null;
    }
}
