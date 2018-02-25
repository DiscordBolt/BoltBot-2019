package com.discordbolt.boltbot.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesUtil {

    public static void updateField(Path propertiesPath, String key, String newValue) throws IOException {
        FileInputStream in = new FileInputStream(propertiesPath.toFile());
        Properties props = new Properties();
        props.load(in);
        in.close();

        FileOutputStream out = new FileOutputStream(propertiesPath.toFile());
        props.setProperty(key, newValue);
        props.store(out, null);
        out.close();
    }
}
