package org.menesty.ikea.tablet;

import java.net.MalformedURLException;
import java.net.URL;

public class Test {
    public static void main(String... arg) throws MalformedURLException {
        URL url = new URL("http://ikea.ho.es/storage/load");
        System.out.println(url.getHost());
        System.out.println(url.getPath());
    }
}
