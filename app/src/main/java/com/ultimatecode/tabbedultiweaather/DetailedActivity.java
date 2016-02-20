package com.ultimatecode.tabbedultiweaather;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class DetailedActivity extends AppCompatActivity {

    private TextView cityNameTextView;
    private TextView descTextView;
    private TextView windValTextView;
    private TextView cloudsValTextView;
    private TextView humidityValTextView;
    private TextView tempValTextView;
    private ImageView cityWeatherImg;
    private Button mapBtn;
    private Button wikiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        // Bind vars to UI elements
        cityNameTextView = (TextView) findViewById(R.id.cityName);
        descTextView = (TextView) findViewById(R.id.weatherDesc);
        windValTextView = (TextView) findViewById(R.id.windVal);
        cloudsValTextView = (TextView) findViewById(R.id.cloudsVal);
        humidityValTextView = (TextView) findViewById(R.id.humidityVal);
        tempValTextView = (TextView) findViewById(R.id.tempVal);

        mapBtn = (Button) findViewById(R.id.mapBtn);
        wikiBtn = (Button) findViewById(R.id.wikiBtn);

        cityWeatherImg = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get city Name TODO check valid not null
        final String cityNameString = getIntent().getStringExtra("city");
        cityNameTextView.setText(cityNameString);
        //maps the wiki and map buttons
        setButtonsListeners(cityNameString);
        // prepare URL
        String url = Utils.CreateWeatherUrl(cityNameString.trim());
        // contact API
        new DownloadWeatherTask().execute(url);
    }

    private void setButtonsListeners(final String cityNameString) {
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
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, CityWeather> {
        @Override
        protected CityWeather doInBackground(String... urls) {
            // execute in background, in separate thread – cannot edit the UI
            // call the method that connects and fetches the data and return the reply
            String jsonRes = null;
            try {
                jsonRes = Utils.downloadUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Utils.ProcessWeatherResponse(jsonRes);
        }

        @Override
        protected void onPostExecute(CityWeather result) {

            // finally, update the TextView accordingly
            String tempString = Utils.getDotlessString(result.getTemp());

            String tempText = tempString + " \u2103";
            String windText = result.getWind() + " km/h";
            String cloudsText = result.getCloud() + " %";
            String humidityText = result.getHumidity() + "%";

            tempValTextView.setText(tempText);
            windValTextView.setText(windText);
            cloudsValTextView.setText(cloudsText);
            humidityValTextView.setText(humidityText);
            descTextView.setText(result.getDesc());

            int imgId = R.drawable.clear;
            if (result.getDesc().contains("rain")) {
                imgId = R.drawable.rainy;
            } else if (result.getDesc().contains("cloud")) {
                imgId = R.drawable.cloudy;
            }

            cityWeatherImg.setImageBitmap(
                    Utils.decodeSampledBitmapFromResource(getResources(), imgId,
                            cityWeatherImg.getWidth(), cityWeatherImg.getHeight()));
        }
    }
}
