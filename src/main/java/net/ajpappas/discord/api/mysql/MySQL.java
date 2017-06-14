package net.ajpappas.discord.api.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.ajpappas.discord.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Tony on 2/12/2017.
 */
public class MySQL {

    private static MysqlDataSource dataSource = null;

    public static Connection getConnection() {
        if (dataSource == null) {
            try {
                Properties props = new Properties();
                FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + File.separator + "database.properties");
                props.load(fis);

                dataSource = new MysqlDataSource();
                dataSource.setUser(props.getProperty("USERNAME"));
                dataSource.setPassword(props.getProperty("PASSWORD"));
                dataSource.setServerName(props.getProperty("HOST"));
                dataSource.setDatabaseName(props.getProperty("DATABASE"));
                return dataSource.getConnection();
            } catch (IOException | SQLException e) {
                Logger.error(e.getMessage());
                Logger.debug(e);
            }
        }
        return null;
    }

    public static boolean isConnected(){
        return getConnection() != null;
    }
}
