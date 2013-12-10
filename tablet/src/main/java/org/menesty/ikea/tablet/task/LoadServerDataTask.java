package org.menesty.ikea.tablet.task;

import android.os.SystemClock;
import org.menesty.ikea.tablet.data.DataLoader;
import org.menesty.ikea.tablet.domain.AvailableProductItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoadServerDataTask extends BaseAsyncTask<Object, Integer, List<AvailableProductItem>> {


    @Override
    protected List<AvailableProductItem> doInBackground(Object... urls) {

        boolean useTmp = true;
            InputStream input = null;
            HttpURLConnection connection = null;
            try {
                if (!useTmp) {
                    URL url = new URL((String) urls[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                        return null;
                    input = connection.getInputStream();
                    onProgressUpdate();
                } else
                    input = tmpData();
                SystemClock.sleep(5000);
                return new DataLoader().readJsonStream(input);


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

    private InputStream tmpData() throws UnsupportedEncodingException {
        String tmpString = "[\n" +
                "   {\n" +
                "     \"productName\": 912345678901,\n" +
                "     \"price\": 50.45,\n" +
                "     \"weight\": 12234,\n" +
                "     \"count\": 2\n" +
                "   }\n" +

                " ]";
        return new ByteArrayInputStream(tmpString.getBytes("UTF-8"));
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
        callbacks.onPostExecute();
        setRunning(false);
    }
}
