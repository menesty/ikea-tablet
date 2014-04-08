package org.menesty.ikea.tablet;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;

public class SettingService {
    private static final String KEY_PREF_SERVER_URL = "pref_server_url";
    private static final String KEY_PREF_USER_LOGIN = "pref_user_login";
    private static final String KEY_PREF_USER_PASSWORD = "pref_user_password";

    public static ApplicationPreferences getSetting(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        ApplicationPreferences preference = new ApplicationPreferences();
        preference.setServerName(preferences.getString(KEY_PREF_SERVER_URL, "http://localhost"));
        preference.setUserName(preferences.getString(KEY_PREF_USER_LOGIN, ""));
        preference.setPassword(preferences.getString(KEY_PREF_USER_PASSWORD, ""));

        return preference;
    }

}
