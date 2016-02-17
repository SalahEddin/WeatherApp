package com.ultimatecode.tabbedultiweaather;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailedActivity extends AppCompatActivity {
    private TextView cityNameTextView;
    private TextView windValTextView;
    private TextView cloudsValTextView;
    private TextView humidityValTextView;
    private TextView tempValTextView;
    private Button mapBtn;
    private Button wikiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        cityNameTextView = (TextView) findViewById(R.id.cityName);
        windValTextView = (TextView) findViewById(R.id.windVal);
        cloudsValTextView = (TextView) findViewById(R.id.cloudsVal);
        humidityValTextView = (TextView) findViewById(R.id.humidityVal);
        tempValTextView = (TextView) findViewById(R.id.tempVal);

        mapBtn = (Button) findViewById(R.id.mapBtn);
        wikiBtn = (Button) findViewById(R.id.wikiBtn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String cityNameString = getIntent().getStringExtra("city");

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("http://maps.google.co.in/maps?q=" + cityNameString);
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

        wikiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://en.wikipedia.org/wiki/" + cityNameString.trim().replace(" ", "_");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // contact API
        AsyncTask<String, Void, CityWeather> finalResponse = new DownloadWeatherTask().execute(
                "http://api.openweathermap.org/data/2.5/weather?q="
                + cityNameString.trim()
                + "&appid=c32028af7342857d87674f1127599ca7");

        cityNameTextView.setText(cityNameString);

    }

    public class DownloadWeatherTask extends AsyncTask<String, Void, CityWeather>{
        @Override protected CityWeather doInBackground(String... urls) {
            // execute in background, in separate thread – cannot edit the UI
            // call the method that connects and fetches the data and return the reply
            return ProcessWeatherResponse(urls[0]);
        }
        @Override
        protected void onPostExecute(CityWeather result) {
            // finally, update the TextView accordingly
            tempValTextView.setText(result.getTemp() + " \u2103");
            windValTextView.setText(result.getWind() + " km/h");
            cloudsValTextView.setText(result.getCloud() + " %");
            humidityValTextView.setText(result.getHumidity() + "%");
        }

        private String downloadUrl(final String urlAddress) throws IOException {
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

            }
            catch (Exception e){
                int xx = 0;
            }
            finally {
                // makes sure that the InputStream is closed after we are finished using it
                if (inputStream != null) {
                    inputStream.close();
                }
                return stringBuilder.toString();// this contains the full reply
            }
        }

        private CityWeather ProcessWeatherResponse(String url){
            //TODO verify internet connection
            CityWeather cityWeather = null;
            try{
                // ... then call the method that connects and fetches the data ...
                JSONObject jsonObject = new JSONObject( downloadUrl(url));
                JSONObject mainJson = jsonObject.getJSONObject("main");
                JSONArray weather = jsonObject.getJSONArray("weather");
                JSONObject singleWeather = weather.getJSONObject(0); // index 0 is first element


                cityWeather = new CityWeather("1",
                        jsonObject.getString("name"),
                        singleWeather.getString("description"),
                        mainJson.getString("temp"),
                        jsonObject.getJSONObject("clouds").getString("all"),
                        jsonObject.getJSONObject("wind").getString("speed"),
                        mainJson.getString("humidity"));
            }
            catch (Exception e){
                int xx = 0;
            }
            return cityWeather;
        }
    }

}
