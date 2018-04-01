package com.discordbolt.boltbot.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
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

    public static Optional<String> getValue(Path propertiesPath, String key) {
        if (!propertiesPath.toFile().exists())
            return Optional.empty();

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesPath.toFile()));
            return Optional.ofNullable(properties.getProperty(key));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
