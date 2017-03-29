package com.litmushealth.gpsdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Rob Agnese
 * (c) 2017 Litmus Health Inc.
 */

final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    static final String name = "GPS Demo Database";
    static final String table = "gps_data";
    static final String columnDate = "timestamp";
    static final String columnLat = "latitude";
    static final String columnLong = "longtidue";

    DatabaseHelper(Context context) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");
        db.execSQL("CREATE TABLE " + table +
                   " (" + columnDate + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                   columnLat + " REAL, " + columnLong + " REAL)");
        Log.d(TAG, "Created table.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No op. This is a demo.
    }
}
