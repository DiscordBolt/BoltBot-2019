package Techtony96.Discord.api.mysql;

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

    private static MysqlDataSource ds;

    public static Connection getConnection() throws SQLException {
       if (ds == null){
           try {
               Properties props = new Properties();
               FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + File.separator + "database.properties");
               props.load(fis);
               ds = new MysqlDataSource();
               ds.setURL(props.getProperty("MYSQL_DB_URL"));
               ds.setUser(props.getProperty("MYSQL_DB_USERNAME"));
               ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return ds.getConnection();
    }
}
