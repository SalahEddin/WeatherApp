package com.ultimatecode.tabbedultiweaather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private Context fragContext;
    private OnFragmentInteractionListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
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
        fragContext = getContext();
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        FloatingActionButton addCityButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
        // event listener to launch the activity to add a new city
        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent which calls for starting the InformationCenterActivity
                Intent intent = new Intent(getContext(), AddCityActivity.class);
                // use the intent to start the pointed activity
                startActivity(intent);
            }
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

        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailedActivity.class);
                intent.putExtra("city", cities.get(position));
                startActivity(intent);

            }
        });

        citiesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final String selectedCity = cities.get(position);

                final CharSequence[] actionsArr = {"Set " + selectedCity + " as Home", "Delete " + selectedCity, "Cancel"};
                Log.d("TAG", "Long clicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(fragContext);

                builder.setTitle(selectedCity + " Actions:")
                        .setItems(actionsArr, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                                        Utils.setHomePref("Banana", getContext());
                                        break;
                                    // cancel
                                    default:
                                        break;
                                }
                            }
                        });
                builder.create().show();
                //builder.show();
                Log.d("TAG", "menu built");
                return true;
            }
        });
    }

    private void DeleteFromDbByName(String selectedCity) {
        // we first need a database open helper to even touch the DB...
        MyDatabaseOpenHelper dbHelper = new MyDatabaseOpenHelper(fragContext);
        // we then get a readable handler to the DB...
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define 'where' part of query.
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf("'" + selectedCity + "'")};
        // Issue SQL statement.
        //db.rawQuery("DELETE FROM cities WHERE NAME = '" + selectedCity+"'", null);

        String selection = "NAME=" + selectionArgs[0];
        db.delete("cities", selection, null);
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
}
