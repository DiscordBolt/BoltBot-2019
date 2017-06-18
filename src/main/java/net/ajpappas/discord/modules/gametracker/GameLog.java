package net.ajpappas.discord.modules.gametracker;

import net.ajpappas.discord.api.mysql.MySQL;
import net.ajpappas.discord.utils.Logger;
import sx.blah.discord.handle.obj.IUser;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony on 2/12/2017.
 */
public class GameLog {

    private static ArrayList<String> cachedGames = new ArrayList<>();

    /**
     * Add a game log entry to the database
     *
     * By Running this method, a few assumptions have been made.
     * 1. The user already exists in the `users` table.
     *
     * @param user
     * @param gameTitle
     * @param startTime
     * @param endTime
     * @return True if successfully added entry
     */
    protected static boolean addGameLog(IUser user, String gameTitle, long startTime, long endTime) {
        if (user.isBot())
            return false;
        if (user == null || startTime == 0 || endTime == 0 || gameTitle == null || gameTitle.length() <= 0)
            throw new IllegalArgumentException("A supplied argument was in an invalid state.");
        if (startTime - endTime > 0)
            throw new IllegalArgumentException("startTime must be before endTime.");

        // Perform a quick cache check to see if the game has already been added
        if (!cachedGames.contains(gameTitle)) {
            addGame(gameTitle);
            cachedGames.add(gameTitle);
        }

        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("INSERT INTO `game_log` (`user`, `game`, `startTime`, `endTime`) VALUES (?, (SELECT `id` FROM `games` WHERE `title` = ?), ?, ?);");
            ps.setLong(1, user.getLongID());
            ps.setString(2, gameTitle);
            ps.setTimestamp(3, new Timestamp(startTime));
            ps.setTimestamp(4, new Timestamp(endTime));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Unable to insert " + user.getName() + "'s gamelog into the database.");
            Logger.debug(e);
            return false;
        }
    }

    /**
     * Add a user to the `users` table.
     * MySQL will automatically replace the old entry.
     *
     * @param user
     * @return Number of rows inserted/updated in the table.
     */
    protected static int addUser(IUser user) {
        if (user.isBot())
            return -1;
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("REPLACE INTO `users` (`id`, `name`) VALUES (?,?);");
            ps.setLong(1, user.getLongID());
            ps.setString(2, user.getName());
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Unable to add " + user.getName() + " to the database.");
            Logger.debug(e);
            return -1;
        }
    }

    protected static int[] addUsers(List<IUser> users) {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("REPLACE INTO `users` (`id`, `name`) VALUES (?,?);");
            for (IUser user : users) {
                if (user.isBot())
                    continue;
                ps.setLong(1, user.getLongID());
                ps.setString(2, user.getName());
                ps.addBatch();
            }
            return ps.executeBatch();
        } catch (SQLException e) {
            Logger.error("Unable to add users to the database.");
            Logger.debug(e);
            return new int[]{-1};
        }
    }

    /**
     * Add a game to the `games` table.
     * MySQL will not add duplicate games
     *
     * @param gameTitle
     * @return Number of rows inserted into the table
     */
    private static int addGame(String gameTitle) {
        try {
            PreparedStatement ps = MySQL.getDataSource().getConnection().prepareStatement("INSERT IGNORE INTO `games` (`title`) VALUES (?);");
            ps.setString(1, gameTitle);
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Unable to add " + gameTitle + " to the database.");
            Logger.debug(e);
            return -1;
        }
    }
}
