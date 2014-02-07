package org.menesty.ikea.tablet;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Properties properties;

    public static void init() {
        properties = new Properties();
        try {
            properties.load(Config.class.getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getServerUrl() {
        return properties.getProperty("serverUrl", "");
    }

    public static String getUser() {
        return properties.getProperty("user");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }
}
