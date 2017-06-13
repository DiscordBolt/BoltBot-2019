package net.ajpappas.discord.modules.seen;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import java.sql.Timestamp;

/**
 * Created by Tony on 5/15/2017.
 */
public class UserStatus {

    private Long userID;
    private Timestamp lastUpdate;
    private StatusType status;

    public UserStatus(IUser user) {
        this.userID = user.getLongID();
        this.status = user.getPresence().getStatus();
        lastUpdate = new Timestamp(System.currentTimeMillis());
    }

    public void updateStatus(StatusType status) {
        this.status = status;
        lastUpdate = new Timestamp(System.currentTimeMillis());
    }

    public Long getUserID() {
        return userID;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public StatusType getStatus() {
        return status;
    }
}
