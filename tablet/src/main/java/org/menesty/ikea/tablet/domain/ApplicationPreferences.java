package org.menesty.ikea.tablet.domain;

import java.util.Locale;

public class ApplicationPreferences {
    private String serverName;

    private String userName;

    private String password;

    private Locale language;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public Locale getLanguage() {
        return language;
    }
}
