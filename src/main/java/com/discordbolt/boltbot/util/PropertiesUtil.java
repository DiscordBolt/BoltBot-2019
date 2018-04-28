package com.discordbolt.boltbot.util;

import com.github.fge.lambdas.Throwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public class PropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
    private static HashMap<Path, Properties> cache = new HashMap<>();

    /**
     * Get a value from the properties file
     *
     * @param propertiesPath The Path of the given properties file
     * @param key            The key to the value requested in the properties file
     * @return Optional<String> value
     * @throws com.github.fge.lambdas.ThrownByLambdaException if an IOException is encountered
     */
    public static Optional<String> getValue(Path propertiesPath, String key) {
        return Optional.ofNullable(cache.computeIfAbsent(propertiesPath, Throwing.function(PropertiesUtil::getPropertiesFile)).getProperty(key));
    }

    public static void loadPropertiesFile(Path... propertiesPath) throws IOException {
        for (Path p : propertiesPath) {
            LOGGER.debug("Loading property file '{}'", p);
            cache.put(p, getPropertiesFile(p));
        }
    }

    private static Properties getPropertiesFile(Path propertiesPath) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(propertiesPath.toFile()));
        return p;
    }

    private static void savePropertiesFile(Path propertiesPath, Properties properties) throws IOException {
        FileOutputStream out = new FileOutputStream(propertiesPath.toFile());
        properties.store(out, null);
        out.close();
    }
}
