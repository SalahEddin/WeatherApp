package com.ultimatecode.tabbedultiweaather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class DetailedFragment extends Fragment {

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

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private CardView MainCardView;
    private CardView DetailedCardView;
    private CardView noNetCardView;


    public DetailedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get the view
        View view = inflater.inflate(R.layout.fragment_detailed, container, false);

        // Bind vars to UI elements
        cityNameTextView = (TextView) view.findViewById(R.id.frag_cityName);
        descTextView = (TextView) view.findViewById(R.id.frag_weatherDesc);
        windValTextView = (TextView) view.findViewById(R.id.frag_wind);
        cloudsValTextView = (TextView) view.findViewById(R.id.frag_clouds);
        humidityValTextView = (TextView) view.findViewById(R.id.frag_humidity);
        tempValTextView = (TextView) view.findViewById(R.id.frag_temp);

        mapBtn = (Button) view.findViewById(R.id.frag_mapBtn);
        wikiBtn = (Button) view.findViewById(R.id.frag_wikiBtn);

        cityWeatherImg = (ImageView) view.findViewById(R.id.WeatherimageView);
        cityWeatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);

        MainCardView = (CardView) view.findViewById(R.id.main_card_view);
        DetailedCardView= (CardView) view.findViewById(R.id.detail_cardview);
        noNetCardView= (CardView) view.findViewById(R.id.no_net_card_view);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //// get home city
        final String homeCityName = Utils.getHomePref(getContext());

        // sharedPreference onchange listener
        listener = (prefs, key) -> {
            if (!key.equals(getContext().getResources().getString(R.string.homeKeyPref)))
                return;

            cityNameTextView.setText(homeCityName);
            setButtonsListeners(homeCityName);
            String url = Utils.CreateWeatherUrl(homeCityName.trim());

            if (Utils.isNetworkAvailable(getContext()) ) {
                new DownloadWeatherTask().execute(url);
                noNetCardView.setVisibility(View.GONE);
            }
            else{
                MainCardView.setVisibility(View.GONE);
                DetailedCardView.setVisibility(View.GONE);

                // TODO: 17/03/16 For Publishing: ask to turn WiFi On
                // WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
                // wifiManager.setWifiEnabled(true);
            }

        };

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        cityNameTextView.setText(homeCityName);
        setButtonsListeners(homeCityName);

        String url = Utils.CreateWeatherUrl(homeCityName.trim());

        if (Utils.isNetworkAvailable(getContext()) ) {
            new DownloadWeatherTask().execute(url);
            noNetCardView.setVisibility(View.GONE);
        }
        else{
            MainCardView.setVisibility(View.GONE);
            DetailedCardView.setVisibility(View.GONE);
            noNetCardView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    // Sets the button functionality to open Wiki page and Map app
    private void setButtonsListeners(final String homeCityName) {
        mapBtn.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("http://maps.google.co.in/maps?q=" + homeCityName);
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent);
        });

        wikiBtn.setOnClickListener(v -> {
            String url = "https://en.wikipedia.org/wiki/" + homeCityName.trim().replace(" ", "_");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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


                Resources resources = DetailedFragment.this.getResources();
                final int resourceId = resources.getIdentifier("r" + result.getIconCode()
                        , "drawable",
                        MainActivity.PACKAGE_NAME);

                cityWeatherIcon.setImageDrawable(resources.getDrawable(resourceId, getContext().getTheme()));
            } catch (Exception e) {
                cityWeatherIcon.setImageResource(R.drawable.unknown);
            }
        }
    }
}
