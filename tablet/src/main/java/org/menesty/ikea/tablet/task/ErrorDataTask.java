package org.menesty.ikea.tablet.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.menesty.ikea.tablet.TabletActivity;
import org.menesty.ikea.tablet.domain.ApplicationPreferences;
import org.menesty.ikea.tablet.util.HttpUtil;

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
            URL url = new URL(desUrl);
            HttpClient httpclient = HttpUtil.createClient();
            StringEntity se = new StringEntity((String) data[1]);


            HttpPost httpPost = new HttpPost(desUrl);
            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost, HttpUtil.createAuthContext(url, setting));

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
