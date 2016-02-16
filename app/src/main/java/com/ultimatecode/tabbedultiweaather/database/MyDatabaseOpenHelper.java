package com.ultimatecode.tabbedultiweaather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by salah on 15/02/16.
 */
public class MyDatabaseOpenHelper extends SQLiteOpenHelper {

    // if you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    // this is used to name the underlying file storing the actual data
    public static final String DATABASE_NAME = "saved_cities.db";

    public MyDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SAVED_CITIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // in our case, we simply delete all data and recreate the DB
        db.execSQL(SQL_DELETE_BLOG_ENTRIES);
        onCreate(db);
    }
    private static final String SQL_CREATE_SAVED_CITIES =
            "CREATE TABLE cities (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "NAME TEXT NOT NULL " +
                    ")";
    private static final String SQL_DELETE_BLOG_ENTRIES =
            "DROP TABLE IF EXISTS entries";
}
