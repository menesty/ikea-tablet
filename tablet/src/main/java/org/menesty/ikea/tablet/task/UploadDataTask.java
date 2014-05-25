package org.menesty.ikea.tablet.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
    public final String UUID;

    public final String data;

    public int tryCount;

    private static final int MAX_TRY_COUNT = 10;

    public UploadDataTask(String uuid, String data) {
        UUID = uuid;
        this.data = data;
    }

    @Override
    protected Boolean doInBackground(Object... data) {
        ApplicationPreferences setting = (ApplicationPreferences) data[0];

        try {
            return start(setting);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean start(ApplicationPreferences setting) throws InterruptedException {
        boolean existOperation = checkOperation(setting);

        if (!existOperation)
            try {
                return upload(setting);
            } catch (Exception e) {
                return start(setting);
            }

        return existOperation;
    }

    private boolean checkOperation(ApplicationPreferences setting) throws InterruptedException {
        Boolean exist = null;

        while (exist == null) {
            try {
                exist = checkOperation(UUID, setting);
            } catch (Exception e) {
                tryCount++;

                if (MAX_TRY_COUNT == tryCount)
                    throw new InterruptedException("Finish limit of connection try");

                Thread.sleep(2000);
            }
        }

        return exist;
    }

    private boolean upload(ApplicationPreferences setting) throws Exception {
        String desUrl = setting.getServerName() + "/paragon/executeExport";

        HttpClient httpclient = HttpUtil.createClient();

        StringEntity se = new StringEntity(this.data);
        URL url = new URL(desUrl);

        HttpPost httpPost = new HttpPost(desUrl);
        httpPost.setEntity(se);

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        HttpResponse httpResponse = httpclient.execute(httpPost, HttpUtil.createAuthContext(url, setting));

        System.out.println(EntityUtils.toString(httpResponse.getEntity()));

        return true;
    }

    private boolean checkOperation(String actionId, ApplicationPreferences setting) throws Exception {
        String desUrl = setting.getServerName() + "/paragon/check/" + actionId;

        HttpClient httpclient = HttpUtil.createClient();
        HttpGet httpGet = new HttpGet(desUrl);

        HttpResponse httpResponse = httpclient.execute(httpGet, HttpUtil.createAuthContext(new URL(desUrl), setting));

        String response = EntityUtils.toString(httpResponse.getEntity());

        return Boolean.valueOf(response);
    }
}
