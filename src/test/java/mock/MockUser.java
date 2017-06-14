package mock;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.cache.LongMap;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Techt on 5/2/2017.
 */
public class MockUser implements IUser {

    private long id;
    private String name;
    private IPresence presence;

    public MockUser(String name, long id, IPresence presence) {
        this.name = name;
        this.id = id;
        this.presence = presence;
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
    public String getDisplayName(IGuild guild) {
        return guild.getName() + ":" + getName();
    }

    @Override
    public IPresence getPresence() {
        return presence;
    }

    @Override
    public String mention() {
        return "<@" + id + ">";
    }

    @Deprecated
    @Override
    public String getAvatar() {
        return null;
    }

    @Deprecated
    @Override
    public String getAvatarURL() {
        return null;
    }

    @Deprecated
    @Override
    public Status getStatus() {
        return null;
    }

    @Deprecated
    @Override
    public String mention(boolean mentionWithNickname) {
        return null;
    }

    @Deprecated
    @Override
    public String getDiscriminator() {
        return null;
    }

    @Deprecated
    @Override
    public List<IRole> getRolesForGuild(IGuild guild) {
        return null;
    }

    @Deprecated
    @Override
    public EnumSet<Permissions> getPermissionsForGuild(IGuild guild) {
        return null;
    }

    @Deprecated
    @Override
    public String getNicknameForGuild(IGuild guild) {
        return null;
    }

    @Deprecated
    @Override
    public IVoiceState getVoiceStateForGuild(IGuild guild) {
        return null;
    }

    @Deprecated
    @Override
    public LongMap<IVoiceState> getVoiceStatesLong() {
        return null;
    }

    @Deprecated
    @Override
    public void moveToVoiceChannel(IVoiceChannel channel) {

    }

    @Deprecated
    @Override
    public boolean isBot() {
        return false;
    }

    @Deprecated
    @Override
    public IPrivateChannel getOrCreatePMChannel() {
        return null;
    }

    @Deprecated
    @Override
    public void addRole(IRole role) {

    }

    @Deprecated
    @Override
    public void removeRole(IRole role) {

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
    public IUser copy() {
        return null;
    }
}
