package Techtony96.Discord.modules.gametracker;

import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Tony on 2/15/2017.
 */
public class UserInfo {

    private IUser user;
    private String game;
    private long startTime;

    public UserInfo(IUser user, String game, long startTime) {
        setUser(user);
        setGame(game);
        setStartTime(startTime);
    }

    public IUser getUser() {
        return user;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
