package org.menesty.ikea.tablet.task;

import org.menesty.ikea.tablet.auth.AuthService;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Menesty on 2/28/14.
 */
public class UploadDataTask extends BaseAsyncTask<Object, Integer, Void> {
    @Override
    protected Void doInBackground(Object... data) {
        HttpURLConnection connection = null;
        String desUrl = data[0] + "/storage/executeExport";

        try {
            URL url = new URL(desUrl);
            connection = (HttpURLConnection) url.openConnection();

            AuthService authService = new AuthService();
            String authHeader = authService.authHeader(desUrl, (String) data[1], (String) data[2], "POST");
            connection.setRequestProperty("Authorization", authHeader);

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + ((String) data[3]).getBytes().length);

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            printout.write(((String) data[3]).getBytes());
            printout.flush();
            printout.close();


            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            java.lang.StringBuffer sb = new java.lang.StringBuffer();
            java.lang.String str = br.readLine();
            while(str != null){
                sb.append(str);
                str = br.readLine();
            }
            br.close();
            java.lang.String responseString = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
}
