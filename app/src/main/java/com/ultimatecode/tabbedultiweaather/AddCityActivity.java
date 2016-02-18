package com.ultimatecode.tabbedultiweaather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.ultimatecode.tabbedultiweaather.database.MyDatabaseOpenHelper;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AddCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        final AutoCompleteTextView cityName = (AutoCompleteTextView) findViewById(R.id.cityNameTextbox);

        Button addButton = (Button) findViewById(R.id.addCity);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clean city string
                String submittedName =  cityName.getText().toString().trim();

                // confirm city exist
                Boolean exists = false;
                try {
                    exists = new CityWeatherExistsTask().execute(Utils.CreateWeatherUrl(submittedName)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(exists){
                    // we first need a database open helper to even touch the DB...
                    MyDatabaseOpenHelper dbHelper = new MyDatabaseOpenHelper(getBaseContext());
                    // we then get a readable handler to the DB...
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    //TODO check if it exists in DB

                    ContentValues values = new ContentValues();
                    values.put("NAME",submittedName);
                    db.insert("cities", null, values);

                    finish();
                }
                else{
                    //TODO notify user
                }
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
            return Utils.Exists(jsonRes);
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }




    }
}
