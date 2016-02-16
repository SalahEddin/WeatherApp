package com.ultimatecode.tabbedultiweaather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        TextView cityNameTextView = (TextView) findViewById(R.id.cityName);

        String cityNameString = getIntent().getStringExtra("city");
        cityNameTextView.setText(cityNameString);
    }
}
