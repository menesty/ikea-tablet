package org.menesty.ikea.tablet.util;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;

import java.net.URL;

public class HttpUtil {

    public static HttpClient createClient() {
        HttpClient httpclient = new DefaultHttpClient();

        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 8 * 1000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 8 * 1000);

        return httpclient;
    }

    public static HttpContext createAuthContext(URL url, ApplicationPreferences setting ){
        AuthScope scope = new AuthScope(url.getHost(), url.getPort());
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(setting.getUserName(), setting.getPassword());
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(scope, creds);

        HttpContext credContext = new BasicHttpContext();
        credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

        return credContext;
    }
}
