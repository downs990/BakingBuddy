package com.downs.bakingbuddy.utilities;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    final static String RECIPE_BASE_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public static URL buildUrl() {
        Uri builtUri = Uri.parse(RECIPE_BASE_URL).buildUpon()
                .build();
        URL myUrl = null;
        try {
            myUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return myUrl;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inStream = urlConnection.getInputStream();
            Scanner myScanner = new Scanner(inStream);
            myScanner.useDelimiter("\\A");

            boolean hasInput = myScanner.hasNext();
            if (hasInput) {
                return myScanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}