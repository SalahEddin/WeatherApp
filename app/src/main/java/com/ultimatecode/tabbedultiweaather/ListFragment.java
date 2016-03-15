package com.ultimatecode.tabbedultiweaather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ultimatecode.tabbedultiweaather.database.MyDatabaseOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.BottomSheetDialog; //// TODO: 25/02/16 switch dialog to bottom sheet

public class ListFragment extends Fragment {

    private Context fragContext;
    private OnFragmentInteractionListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragContext = getContext();
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        FloatingActionButton addCityButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
        // event listener to launch the activity to add a new city
        addCityButton.setOnClickListener(v -> {
            // create an intent which calls for starting the AddCityActivity
            Intent intent = new Intent(getContext(), AddCityActivity.class);
            // use the intent to start the pointed activity
            startActivity(intent);
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // for fragments, findViewById here
        ListView citiesListView = (ListView) getView().findViewById(R.id.citiesListView);

        // we first need a database open helper to even touch the DB...
        MyDatabaseOpenHelper dbHelper = new MyDatabaseOpenHelper(getContext());
        // we then get a readable handler to the DB...
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // and then we run a raw SQL query which returns a cursor pointing to the results
        Cursor cursor = db.rawQuery("SELECT * FROM cities", null);

        // number of rows in the result set
        int numOfRows = cursor.getCount();

        // initialise with dummy element TODO

        final List<String> cities = new ArrayList<>();

        // if DB holds elements
        if (numOfRows > 0) {

            cursor.moveToFirst();
            int columnNameIndex = cursor.getColumnIndex("NAME");

            for (int i = 0; i < numOfRows; i++) {
                cities.add(cursor.getString(columnNameIndex));
                cursor.moveToNext();
            }
            // sort alphabetically
            Collections.sort(cities);
        } else {
            cities.add("Nicosia"); //// TODO: 18/02/16 prompt to add cities
        }

        cursor.close();

        // build adapter
        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                cities);

        citiesListView.setAdapter(cityAdapter);

        citiesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), DetailedActivity.class);
            intent.putExtra("city", cities.get(position));
            startActivity(intent);

        });

        citiesListView.setOnItemLongClickListener((parent, view, position, id) -> {
            final String selectedCity = cities.get(position);

            final CharSequence[] actionsArr = {"Set " + selectedCity + " as Home", "Delete " + selectedCity, "Cancel"};
            Log.d("TAG", "Long clicked");

            AlertDialog.Builder builder = new AlertDialog.Builder(fragContext);

            builder.setTitle(selectedCity + " Actions:")
                    .setItems(actionsArr, (dialog, which) -> {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            // set as home
                            case 0:
                                Utils.setHomePref(selectedCity, getContext());
                                break;
                            // delete item
                            case 1:
                                cities.remove(position);
                                cityAdapter.notifyDataSetChanged();
                                DeleteFromDbByName(selectedCity);
                                // set home to default
                                //// TODO: 26/02/16 get GPS coordinates
                                Utils.setHomePref("Banana", getContext());
                                break;
                            // cancel
                            default:
                                break;
                        }
                    });
            builder.create().show();
            return true;
        });
    }

    private void DeleteFromDbByName(String selectedCity) {
        // we first need a database open helper to even touch the DB...
        MyDatabaseOpenHelper dbHelper = new MyDatabaseOpenHelper(fragContext);
        // we then get a readable handler to the DB...
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf("'" + selectedCity + "'")};
        // Issue SQL statement.
        String selection = "NAME=" + selectionArgs[0];
        db.delete("cities", selection, null);
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
}
