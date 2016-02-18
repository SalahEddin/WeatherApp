package com.ultimatecode.tabbedultiweaather;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by salah on 18/02/16.
 */

public class Utils {

    public static String getDotlessString(String tempString) {
        // check if "." exists in the returned value
        int subStrEnd = tempString.indexOf(".");
        if (subStrEnd > 0)
            tempString = tempString.substring(0, subStrEnd); // remove . for readability
        return tempString;
    }

    // forms the URL for openWeatherAPI
    public static String CreateWeatherUrl(String cityName) {
        return "http://api.openweathermap.org/data/2.5/weather?q="
                + cityName
                + "&appid=c32028af7342857d87674f1127599ca7"
                + "&units=metric";
    }

    public static String downloadUrl(final String urlAddress) throws IOException {
        InputStream inputStream = null;
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // this is the default anyway
            conn.setDoInput(true); // connections can be used for input or output
            conn.setDoOutput(true);
            conn.connect(); // connects and starts the query
            int response = conn.getResponseCode(); // should be 200 if all is OK TODO
            inputStream = conn.getInputStream();
            // handle response (which can be accessed via the ‘inputStream’)
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"), 8);
            // the string builder is used to read all the bytes into a single string

            String line; // used as a temporary buffer
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

        } catch (Exception e) {
            Log.d("Tag", e.toString());
        } finally {
            // makes sure that the InputStream is closed after we are finished using it
            if (inputStream != null) {
                inputStream.close();
            }
            return stringBuilder.toString();// this contains the full reply
        }
    }



    // Methods to help display large images without running out of memory

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    // methods concerning Weather
    public static CityWeather ProcessWeatherResponse(String jsonResponse) {
        //TODO verify internet connection
        CityWeather cityWeather = null;
        try {
            // ... then call the method that connects and fetches the data ...
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject mainJson = jsonObject.getJSONObject("main");
            JSONArray weather = jsonObject.getJSONArray("weather");
            JSONObject singleWeather = weather.getJSONObject(0); // index 0 is first element


            cityWeather = new CityWeather(
                    jsonObject.getString("name"),
                    singleWeather.getString("description"),
                    mainJson.getString("temp"),
                    jsonObject.getJSONObject("clouds").getString("all"),
                    jsonObject.getJSONObject("wind").getString("speed"),
                    mainJson.getString("humidity"));
        } catch (Exception e) {
            int xx = 0;
        }
        return cityWeather;
    }

    public static Boolean Exists(String response) {

        String cod = "404";
        try {
            JSONObject jsonObject = new JSONObject(response);
            cod = jsonObject.getString("cod");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cod.equals("200");
    }
}
