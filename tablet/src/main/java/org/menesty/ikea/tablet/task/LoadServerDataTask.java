package org.menesty.ikea.tablet.task;

import org.menesty.ikea.tablet.auth.AuthService;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;
import org.menesty.ikea.tablet.domain.AvailableProductItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoadServerDataTask extends BaseAsyncTask<Object, Integer, List<AvailableProductItem>> {

    @Override
    protected List<AvailableProductItem> doInBackground(Object... data) {
        ApplicationPreferences setting = (ApplicationPreferences) data[0];

        InputStream input = null;
        HttpURLConnection connection = null;

        String desUrl = setting.getServerName() + "/storage/load";

        AuthService authService = new AuthService();
        try {
            URL url = new URL(desUrl);
            connection = (HttpURLConnection) url.openConnection();

            String authHeader = authService.authHeader(desUrl, setting.getUserName(), setting.getPassword(), "GET");
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

}
