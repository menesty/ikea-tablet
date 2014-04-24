package org.menesty.ikea.tablet.task;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.menesty.ikea.tablet.TabletActivity;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;

import java.net.URL;

/**
 * Created by Menesty on 2/28/14.
 */
public class ErrorDataTask extends BaseAsyncTask<Object, Integer, Void> {
    @Override
    protected Void doInBackground(Object... data) {
        ApplicationPreferences setting = (ApplicationPreferences) data[0];
        String desUrl = setting.getServerName() + "/error/tablet";

        try {
            HttpClient httpclient = new DefaultHttpClient();
            StringEntity se = new StringEntity((String) data[1]);
            URL url = new URL(desUrl);

            AuthScope scope = new AuthScope(url.getHost(), url.getPort());
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(setting.getUserName(), setting.getPassword());
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            HttpPost httpPost = new HttpPost(desUrl);
            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost, credContext);

            String result = EntityUtils.toString(httpResponse.getEntity());
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            TabletActivity.get().fatalClose();
        }

        return null;
    }


}
