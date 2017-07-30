package net.ajpappas.discord.api.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.ajpappas.discord.utils.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by Tony on 2/12/2017.
 */
public class MySQL {

    private static MysqlDataSource dataSource = null;
    private static Path propertiesPath = Paths.get(System.getProperty("user.dir"), "database.properties");

    private static void init() {
        OutputStream output = null;
        try {
            if (!propertiesPath.toFile().exists()){
                Properties prop = new Properties();
                output = new FileOutputStream(propertiesPath.toFile());
                prop.setProperty("USERNAME", "root");
                prop.setProperty("PASSWORD", "password");
                prop.setProperty("HOST", "localhost");
                prop.setProperty("DATABASE", "Discord.java");
                prop.store(output, "Discord.java MySQL Configuration");
            }
        } catch (IOException e){
            Logger.error(e.getMessage());
            Logger.debug(e);
        } finally {
            if (output != null){
                try {
                    output.close();
                } catch (IOException e){
                    Logger.error("Could not close MySQL Properties file.");
                    Logger.debug(e);
                }
            }
        }
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesPath.toFile()));
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
