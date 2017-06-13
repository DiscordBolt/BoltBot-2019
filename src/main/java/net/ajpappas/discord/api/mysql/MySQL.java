package Techtony96.Discord.api.mysql;

import Techtony96.Discord.utils.Logger;
import com.mysql.cj.jdbc.MysqlDataSource;

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

    public static Connection getConnection() throws SQLException {
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
            } catch (IOException e) {
                Logger.debug(e);
            }
        }
        return dataSource.getConnection();
    }

    private void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
