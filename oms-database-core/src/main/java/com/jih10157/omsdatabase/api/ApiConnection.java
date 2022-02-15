package com.jih10157.omsdatabase.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiConnection {

    public static String getString(String resource) throws IOException {
        URL url = new URL("https://userdb.ourmc.space/api/v1/" + resource);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        String returnString;
        try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            returnString = bufferedReader.readLine();
        }
        conn.disconnect();
        return returnString;
    }
}
