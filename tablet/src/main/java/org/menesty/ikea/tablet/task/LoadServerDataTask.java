package org.menesty.ikea.tablet.task;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;
import org.menesty.ikea.tablet.domain.AvailableProductItem;

import java.net.URL;
import java.util.List;

public class LoadServerDataTask extends BaseAsyncTask<Object, Integer, List<AvailableProductItem>> {

    @Override
    protected List<AvailableProductItem> doInBackground(Object... data) {
        ApplicationPreferences setting = (ApplicationPreferences) data[0];

        HttpClient httpclient;

        String desUrl = setting.getServerName() + "/storage/load";

        try {
            httpclient = new DefaultHttpClient();

            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 8 * 1000);
            HttpConnectionParams.setSoTimeout(httpclient.getParams(), 8 * 1000);

            URL url = new URL(desUrl);

            AuthScope scope = new AuthScope(url.getHost(), url.getPort());
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(setting.getUserName(), setting.getPassword());
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            HttpGet httpGet = new HttpGet(desUrl);
            HttpResponse httpResponse = httpclient.execute(httpGet, credContext);

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return null;

            onProgressUpdate(100);

            return new DataJsonService().parseProducts(httpResponse.getEntity().getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
