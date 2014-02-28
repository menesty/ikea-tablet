package org.menesty.ikea.tablet.auth;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Random;

public class AuthService {

    public String authHeader(String desUrl, String user, String password, String method) throws Exception {
        String authHeader = "";

        URL url = new URL(desUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            String wwwAuthHeader = connection.getHeaderField("Www-Authenticate");

            if (wwwAuthHeader != null && wwwAuthHeader.contains("Digest"))
                authHeader = generateAuthHeader(wwwAuthHeader, user, password, method, url.getPath());
        } else
            authHeader = connection.getRequestProperty("Authorization");

        return authHeader;
    }

    public String generateAuthHeader(String wwwAuth, String user, String pass, String httpMethod, String uri) throws Exception {
        String data = wwwAuth.substring(wwwAuth.indexOf(" ") + 1);

        MessageDigest digest = MessageDigest.getInstance("MD5");

        HashMap<String, String> x = parseHttpDigest(data);

        String realm = x.get("realm");
        String A1 = encrypt(digest, user + ":" + realm + ":" + pass);
        String A2 = encrypt(digest, httpMethod + ":" + uri);

        String cnonce = Integer.toString(Math.abs(new Random().nextInt()));
        String ncvalue = "00000001";
        String nonce = x.get("nonce");
        String qop = x.get("qop");
        String opaque = x.get("opaque");

        String responseSeed = A1 + ":" + nonce + ":" + ncvalue + ":" + cnonce + ":" + qop + ":" + A2;
        String response = encrypt(digest, responseSeed);

        String authorizationHeader = "Digest username=\"" + user + "\", realm=\"";
        authorizationHeader += realm + "\", nonce=\"" + nonce + "\",";
        authorizationHeader += " uri=\"" + uri + "\", cnonce=\"" + cnonce;
        authorizationHeader += "\", nc=" + ncvalue + ", response=\"" + response + "\", qop=" + qop;
        authorizationHeader += ", opaque=\"" + opaque + "\"";

        return authorizationHeader;
    }

    HashMap<String, String> parseHttpDigest(String parts) {
        HashMap<String, String> data = new HashMap<String, String>();

        int equalIndex;

        while ((equalIndex = parts.indexOf("=")) >= 0) {
            String partName = parts.substring(0, equalIndex).trim();

            int endIndex = parts.indexOf(",", equalIndex);
            if (endIndex == -1)
                endIndex = parts.length();

            String partValue = parts.substring(equalIndex + 1, endIndex).trim();

            if (partValue.charAt(0) == '"')
                partValue = partValue.substring(1, partValue.length() - 1);

            data.put(partName, partValue);

            if (endIndex == parts.length())
                break;

            parts = parts.substring(endIndex + 1);
        }

        return data;
    }

    String encrypt(MessageDigest digest, String txt) throws Exception {
        byte[] txtBytes = txt.getBytes();
        byte[] hashedBytes = new byte[16];
        digest.update(txtBytes, 0, txtBytes.length);

        int bytesNum = digest.digest(hashedBytes, 0, hashedBytes.length);
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytesNum; i++) {
            String hex = Integer.toHexString(0xFF & hashedBytes[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}