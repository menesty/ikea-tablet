package org.menesty.ikea.tablet.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;
import org.menesty.ikea.tablet.util.HttpUtil;

import java.net.URL;

/**
 * Created by Menesty on
 * 5/25/14.
 * 20:32.
 */
public class CancelParagonTask extends BaseAsyncTask<Object, Integer, Boolean> {
    public final String UUID;

    public CancelParagonTask(String uuid) {
        UUID = uuid;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        ApplicationPreferences setting = (ApplicationPreferences) params[0];

        String desUrl = setting.getServerName() + "/paragon/cancelByActionId/" + UUID;

        HttpClient httpclient = HttpUtil.createClient();
        HttpGet httpGet = new HttpGet(desUrl);

        try {
            HttpResponse httpResponse = httpclient.execute(httpGet, HttpUtil.createAuthContext(new URL(desUrl), setting));

            String response = EntityUtils.toString(httpResponse.getEntity());

            return Boolean.valueOf(response);

        } catch (Exception e) {
            return false;
        }
    }
}
