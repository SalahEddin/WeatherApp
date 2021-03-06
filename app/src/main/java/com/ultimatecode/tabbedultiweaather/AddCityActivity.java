package com.ultimatecode.tabbedultiweaather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ultimatecode.tabbedultiweaather.database.MyDatabaseOpenHelper;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AddCityActivity extends AppCompatActivity {
    private Context context;
    private CheckBox HomeCheckbox;
    private TextView ErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        context = this;

        // binding
        final EditText cityName = (EditText) findViewById(R.id.cityNameTextbox);
        HomeCheckbox = (CheckBox) findViewById(R.id.homeCheckBox);
        ErrorTextView = (TextView) findViewById(R.id.errorTextView);
        Button addButton = (Button) findViewById(R.id.addCity);
        addButton.setOnClickListener(v -> {
            //clean city string
            String submittedName = cityName.getText().toString().trim();
            // confirm city exist
            Boolean exists = false;
            try {
                exists = new CityWeatherExistsTask().execute(
                        Utils.CreateWeatherUrl(submittedName)).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (exists) {
                // we first need a database open helper to touch the DB...
                MyDatabaseOpenHelper dbHelper = new MyDatabaseOpenHelper(getBaseContext());
                // we then get a readable handler to the DB...
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                //check if entry already exists in DB
                boolean inDbAlready = Utils.alreadyAdded(submittedName, context);

                if (inDbAlready) {
                    ErrorTextView.setText("WOW! you really like " + submittedName + "? it's already in your list");
                    // TODO: 17/03/16 should I remove toasts for better UX?
                    // CharSequence msg = "" + submittedName + " is already in your list";
                    //Toast.makeText(AddCityActivity.this, msg, Toast.LENGTH_LONG).show();
                } else {
                    // Add city to list
                    if (HomeCheckbox.isChecked()) Utils.setHomePref(submittedName, context);
                    ContentValues values = new ContentValues();
                    values.put("NAME", submittedName);
                    db.insert("cities", null, values);

                    finish();
                }
            } else {
                ErrorTextView.setText("Like unicorns..." + submittedName + " city does not exist");
                // TODO: 17/03/16 should I remove toasts for better UX?
                // CharSequence msg = submittedName + " is not in our cities list";
                // Toast.makeText(AddCityActivity.this, msg, Toast.LENGTH_LONG).show();

            }
        });
    }

    private class CityWeatherExistsTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            // execute in background, in separate thread – cannot edit the UI
            // call the method that connects and fetches the data and return the reply
            String jsonRes = null;
            try {
                jsonRes = Utils.downloadUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Utils.ExistsResponse(jsonRes);
        }
    }
}
