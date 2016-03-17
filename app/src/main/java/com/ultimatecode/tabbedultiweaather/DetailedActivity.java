package com.ultimatecode.tabbedultiweaather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ultimatecode.tabbedultiweaather.database.MyDatabaseOpenHelper;

import java.io.IOException;

public class DetailedActivity extends Activity {

    // References to UI elements
    private TextView cityNameTextView;
    private TextView descTextView;
    private TextView windValTextView;
    private TextView cloudsValTextView;
    private TextView humidityValTextView;
    private TextView tempValTextView;

    private ImageView cityWeatherImg;
    private ImageView cityWeatherIcon;

    private Button mapBtn;
    private Button wikiBtn;
    private Button deleteCardBtn;
    private Button setHomeCardBtn;

    private CardView MainCardView;
    private CardView DetailedCardView;
    private CardView noNetCardView;
    private CardView optionsCardView;
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
        deleteCardBtn = (Button) findViewById(R.id.deleteCardBtn);
        setHomeCardBtn = (Button) findViewById(R.id.setHomeCardBtn);
        cityWeatherImg = (ImageView) findViewById(R.id.WeatherimageView);
        cityWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        MainCardView = (CardView) findViewById(R.id.main_card_view);
        DetailedCardView= (CardView) findViewById(R.id.detail_cardview);
        noNetCardView= (CardView) findViewById(R.id.no_net_card_view);
        optionsCardView = (CardView) findViewById(R.id.optionsCardView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get city Name
        final String cityNameString = getIntent().getStringExtra("city");
        cityNameTextView.setText(cityNameString);
        //maps the wiki and map buttons
        setButtonsListeners(cityNameString);
        // prepare URL
        String url = Utils.CreateWeatherUrl(cityNameString.trim());

        // if there's no Internet connection, then hide the weather cards
        if (Utils.isNetworkAvailable(getApplicationContext()) ) {
            new DownloadWeatherTask().execute(url);
            MainCardView.setVisibility(View.VISIBLE);
            DetailedCardView.setVisibility(View.VISIBLE);
            optionsCardView.setVisibility(View.VISIBLE);
            noNetCardView.setVisibility(View.GONE);
        }
        else{
            MainCardView.setVisibility(View.GONE);
            DetailedCardView.setVisibility(View.GONE);
            optionsCardView.setVisibility(View.GONE);
            noNetCardView.setVisibility(View.VISIBLE);
        }
    }

    // currently use name of city, change to log, lan for Maps
    private void setButtonsListeners(final String cityNameString) {
        mapBtn.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("http://maps.google.co.in/maps?q=" + cityNameString);
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent);
        });

        wikiBtn.setOnClickListener(v -> {
            String url = "https://en.wikipedia.org/wiki/" + cityNameString.trim().replace(" ", "_");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        // logic of delete button
        deleteCardBtn.setOnClickListener(v -> {
            // Confirmation Dialog
            new AlertDialog.Builder(DetailedActivity.this)
                    .setTitle("Delete" + cityNameString)
                    .setMessage("\t Are you sure you want to delete " + cityNameString + "? \n (Don't worry, you can add it again)")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("Delete ", (dialog, whichButton) -> {
                        DeleteFromDbByName(cityNameString);
                        Toast.makeText(DetailedActivity.this, cityNameString + " successfully removed", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton(android.R.string.cancel, null).show();
        });
        // Sets the city as home city
        setHomeCardBtn.setOnClickListener(v -> {
            Utils.setHomePref(cityNameString, DetailedActivity.this);
            Toast.makeText(DetailedActivity.this, "Set " + cityNameString + " as Home", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void DeleteFromDbByName(String selectedCity) {
        // we first need a database open helper to even touch the DB...
        MyDatabaseOpenHelper dbHelper = new MyDatabaseOpenHelper(getApplicationContext());
        // we then get a readable handler to the DB...
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf("'" + selectedCity + "'")};
        // Issue SQL statement.
        String selection = "NAME=" + selectionArgs[0];
        db.delete("cities", selection, null);
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
            try {


                Resources resources = DetailedActivity.this.getResources();
                final int resourceId = resources.getIdentifier("r" + result.getIconCode()
                        , "drawable",
                        DetailedActivity.this.getPackageName());

                cityWeatherIcon.setImageDrawable(resources.getDrawable(resourceId, DetailedActivity.this.getTheme()));
            } catch (Exception e) {
                cityWeatherIcon.setImageResource(R.drawable.unknown);
            }
        }
    }
}
