package org.menesty.ikea.tablet.task;

import org.menesty.ikea.tablet.auth.AuthService;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.domain.AvailableProductItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoadServerDataTask extends BaseAsyncTask<Object, Integer, List<AvailableProductItem>> {


    @Override
    protected List<AvailableProductItem> doInBackground(Object... data) {
        InputStream input = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(data[0] + "/storage/load");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                String wwwAuthHeader = connection.getHeaderField("Www-Authenticate");

                if (wwwAuthHeader != null && wwwAuthHeader.contains("Digest")) {
                    AuthService authService = new AuthService();

                    String authHeader = authService.generateAuthHeader(wwwAuthHeader, (String) data[1], (String) data[2], "GET", url.getPath());

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization", authHeader);

                    connection.connect();
                }

            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            input = connection.getInputStream();
            onProgressUpdate(100);

            return new DataJsonService().parseProducts(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
    }

    @Override
    protected void onPreExecute() {
        callbacks.onPreExecute();
        setRunning(true);
    }

    @Override
    protected void onProgressUpdate(Integer... percent) {
        callbacks.onProgressUpdate(percent[0]);
    }

    @Override
    protected void onCancelled() {
        callbacks.onCancelled();
        setRunning(false);
    }

    @Override
    protected void onPostExecute(List<AvailableProductItem> result) {
        callbacks.onPostExecute(this, result);
        setRunning(false);
    }
}
