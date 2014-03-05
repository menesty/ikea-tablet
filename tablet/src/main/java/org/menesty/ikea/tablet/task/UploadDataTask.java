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
import org.menesty.ikea.tablet.auth.AuthService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Menesty on 2/28/14.
 */
public class UploadDataTask extends BaseAsyncTask<Object, Integer, Void> {
    @Override
    protected Void doInBackground(Object... data) {
        String desUrl = data[0] + "/storage/executeExport";
        try {
            AuthService authService = new AuthService();
            String authHeader = authService.authHeader(desUrl, (String) data[1], (String) data[2], "POST");


            HttpClient httpclient = new DefaultHttpClient();
            StringEntity se = new StringEntity((String) data[3]);
            URL url = new URL(desUrl);

            AuthScope scope = new AuthScope(url.getHost(), url.getPort());
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials((String) data[1], (String) data[2]);
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            HttpPost httpPost = new HttpPost(desUrl);
            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", authHeader);

            HttpResponse httpResponse = httpclient.execute(httpPost, credContext);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = convertInputStreamToString(inputStream);
            System.out.println(result);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
