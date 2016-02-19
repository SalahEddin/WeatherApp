package com.ultimatecode.tabbedultiweaather;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView cityNameTextView;
    private TextView descTextView;
    private TextView windValTextView;
    private TextView cloudsValTextView;
    private TextView humidityValTextView;
    private TextView tempValTextView;
    private ImageView cityWeatherImg;
    private Button mapBtn;
    private Button wikiBtn;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DetailedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailedFragment newInstance(String param1, String param2) {
        DetailedFragment fragment = new DetailedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        setButtonsListeners(homeCityName);

        String url = Utils.CreateWeatherUrl(homeCityName.trim());
        // contact API
        new DownloadWeatherTask().execute(url);

        cityNameTextView.setText(homeCityName);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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

            cityWeatherImg.setImageBitmap(
                    Utils.decodeSampledBitmapFromResource(getResources(), R.drawable.stormloop, 230, 230));
        }


    }
}
