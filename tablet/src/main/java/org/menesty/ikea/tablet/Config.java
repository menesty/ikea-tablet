package org.menesty.ikea.tablet;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Properties properties;

    public static void init(Context context) {
        properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            properties.load(assetManager.open("config.properties"));
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
