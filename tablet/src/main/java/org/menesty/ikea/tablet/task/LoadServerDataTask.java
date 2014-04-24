package org.menesty.ikea.tablet.task;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;
import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.util.HttpUtil;

import java.net.URL;
import java.util.List;

public class LoadServerDataTask extends BaseAsyncTask<Object, Integer, List<AvailableProductItem>> {

    @Override
    protected List<AvailableProductItem> doInBackground(Object... data) {
        ApplicationPreferences setting = (ApplicationPreferences) data[0];
        String desUrl = setting.getServerName() + "/storage/load";

        try {
            URL url = new URL(desUrl);
            HttpClient httpclient = HttpUtil.createClient();

            HttpGet httpGet = new HttpGet(desUrl);
            HttpResponse httpResponse = httpclient.execute(httpGet, HttpUtil.createAuthContext(url, setting));

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
