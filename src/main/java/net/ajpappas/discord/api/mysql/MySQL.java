package net.ajpappas.discord.api.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.ajpappas.discord.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Tony on 2/12/2017.
 */
public class MySQL {

    private static MysqlDataSource dataSource = null;

    private static void init() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(System.getProperty("user.dir") + File.separator + "database.properties"));
            dataSource = new MysqlDataSource();
            dataSource.setUser(props.getProperty("USERNAME"));
            dataSource.setPassword(props.getProperty("PASSWORD"));
            dataSource.setServerName(props.getProperty("HOST"));
            dataSource.setDatabaseName(props.getProperty("DATABASE"));
        } catch (IOException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
        }
    }

    public static MysqlDataSource getDataSource() {
        if (dataSource == null)
            init();
        return dataSource;
    }
}
