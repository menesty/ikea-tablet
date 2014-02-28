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
        String desUrl = data[0] + "/storage/load";

        AuthService authService = new AuthService();
        try {
            URL url = new URL(desUrl);
            connection = (HttpURLConnection) url.openConnection();

            String authHeader = authService.authHeader(desUrl, (String) data[1], (String) data[2], "GET");
            connection.setRequestProperty("Authorization", authHeader);

            connection.connect();

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
