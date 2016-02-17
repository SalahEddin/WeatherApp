package com.ultimatecode.tabbedultiweaather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.ultimatecode.tabbedultiweaather.database.MyDatabaseOpenHelper;

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
                // TODO confirm city exist
                if(true){
                    // we first need a database open helper to even touch the DB...
                    MyDatabaseOpenHelper dbHelper = new MyDatabaseOpenHelper(getBaseContext());
                    // we then get a readable handler to the DB...
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    //TODO check if it exists in DB

                    ContentValues values = new ContentValues();
                    values.put("NAME",cityName.getText().toString());
                    db.insert("cities", null, values);

                    finish();
                }
                else{
                    //TODO notify user
                }
            }
        });
    }
}
