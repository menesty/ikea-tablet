package org.menesty.ikea.tablet.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;
import org.menesty.ikea.tablet.util.HttpUtil;

import java.net.URL;

/**
 * Created by Menesty on 2/28/14.
 */
public class UploadDataTask extends BaseAsyncTask<Object, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(Object... data) {
        ApplicationPreferences setting = (ApplicationPreferences) data[0];
        String desUrl = setting.getServerName() + "/paragon/executeExport";

        try {
            HttpClient httpclient = HttpUtil.createClient();
            StringEntity se = new StringEntity((String) data[1]);
            URL url = new URL(desUrl);

            HttpPost httpPost = new HttpPost(desUrl);
            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost, HttpUtil.createAuthContext(url, setting));

            System.out.println(EntityUtils.toString(httpResponse.getEntity()));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
