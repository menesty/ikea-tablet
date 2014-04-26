package org.menesty.ikea.tablet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;

import java.util.Locale;
import java.util.Properties;

public class SettingService {
    private static final String KEY_PREF_SERVER_URL = "pref_server_url";
    private static final String KEY_PREF_USER_LOGIN = "pref_user_login";
    private static final String KEY_PREF_USER_PASSWORD = "pref_user_password";
    private static final String KEY_PREF_LANGUAGE = "pref_user_language";

    private static Properties properties;

    public static void init(Context context) {
        properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            properties.load(assetManager.open("config.properties"));
        } catch (Exception e) {
            properties = new Properties();
        }
    }

    private static String getDefaultServerUrl() {
        return properties.getProperty("serverUrl", "");
    }

    private static String getDefaultUser() {
        return properties.getProperty("user");
    }

    private static String getDefaultPassword() {
        return properties.getProperty("password");
    }

    private static Locale getDefaultLanguage() {
        return new Locale(properties.getProperty("language", "en"));
    }

    public static ApplicationPreferences getSetting(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        ApplicationPreferences preference = new ApplicationPreferences();

        preference.setServerName(preferences.getString(KEY_PREF_SERVER_URL, getDefaultServerUrl()));
        preference.setUserName(preferences.getString(KEY_PREF_USER_LOGIN, getDefaultUser()));
        preference.setPassword(preferences.getString(KEY_PREF_USER_PASSWORD, getDefaultPassword()));
        String lang = preferences.getString(KEY_PREF_LANGUAGE, null);
        preference.setLanguage(lang == null ? getDefaultLanguage() : new Locale(lang));

        return preference;
    }

}
