package Techtony96.Discord.modules.gametracker;

import Techtony96.Discord.api.mysql.MySQL;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Tony on 2/12/2017.
 */
public class GameLog {

    private static ArrayList<String> cachedGames = new ArrayList<>();

    /**
     * Add a game log entry to the database
     *
     * By Running this method, a few assumptions have been made.
     *      1. The user already exists in the `users` table.
     * @param user
     * @param gameTitle
     * @param startTime
     * @param endTime
     * @return True if successfully added entry
     */
    protected static boolean addGameLog(IUser user, String gameTitle, Date startTime, Date endTime) {
        if (user == null || startTime == null || endTime == null || gameTitle == null || gameTitle.length() <= 0)
            throw new IllegalArgumentException("A supplied argument was in an invalid state.");
        if (startTime.after(endTime))
            throw new IllegalArgumentException("startTime must be before endTime.");

        // Perform a quick cache check to see if the game has already been added
        if (!cachedGames.contains(gameTitle)){
            addGame(gameTitle);
            cachedGames.add(gameTitle);
        }

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO `Discord.java`.`game_log` (`user`, `game`, `startTime`, `endTime`) VALUES (?, (SELECT `id` FROM `Discord.java`.`games` WHERE `title` = ?), ?, ?);");
            ps.setString(1, user.getID());
            ps.setString(2, gameTitle);
            ps.setDate(3, startTime);
            ps.setDate(4, endTime);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
            throw new IllegalStateException("The MySQL Query could not be completed.", e);
        }
    }


    /**
     * Add a user to the `users` table.
     * MySQL will automatically replace the old entry.
     * @param user
     * @return Number of rows inserted/updated in the table.
     */
    private static int addUser(IUser user) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("REPLACE INTO `Discord.java`.`users` (`id`, `name`) VALUES (?,?);");
            ps.setString(1, user.getID());
            ps.setString(2, user.getName());
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
            throw new IllegalStateException("The MySQL Query could not be completed.", e);
        }
    }

    /**
     * Add a game to the `games` table.
     * MySQL will not add duplicate games
     * @param gameTitle
     * @return Number of rows inserted into the table
     */
    private static int addGame(String gameTitle) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT IGNORE INTO `Discord.java`.`games` (`title`) VALUES (?);");
            ps.setString(1, gameTitle);
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
            throw new IllegalStateException("The MySQL Query could not be completed.", e);
        }
    }


}
