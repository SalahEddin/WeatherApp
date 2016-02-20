package com.ultimatecode.tabbedultiweaather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
    private Button mapBtn;
    private Button wikiBtn;

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

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

        cityWeatherImg = (ImageView) view.findViewById(R.id.frag_weatherImg);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //// get home city
        final String homeCityName = Utils.getHomePref(getContext());

        // sharedPreference onchange listener
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (!key.equals(getContext().getResources().getString(R.string.homeKeyPref)))
                    return;

                cityNameTextView.setText(homeCityName);
                setButtonsListeners(homeCityName);
                String url = Utils.CreateWeatherUrl(homeCityName.trim());
                new DownloadWeatherTask().execute(url);
            }
        };

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        cityNameTextView.setText(homeCityName);
        setButtonsListeners(homeCityName);

        String url = Utils.CreateWeatherUrl(homeCityName.trim());
        // contact API
        new DownloadWeatherTask().execute(url);
    }

    @Override
    public void onPause() {
        super.onPause();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    // Sets the button functionality to open Wiki page and Map app
    private void setButtonsListeners(final String homeCityName) {
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("http://maps.google.co.in/maps?q=" + homeCityName);
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
                String url = "https://en.wikipedia.org/wiki/" + homeCityName.trim().replace(" ", "_");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
