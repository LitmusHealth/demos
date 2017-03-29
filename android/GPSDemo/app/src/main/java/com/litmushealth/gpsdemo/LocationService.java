package com.litmushealth.gpsdemo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Rob Agnese
 *         (c) 2017 Litmus Health
 */

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    // Some constants for interacting with the service
    public static final String BUNDLE_KEY = "location_bundle_key";
    public static final String UPDATE_INTERVAL_MILLIS_KEY = "update_interval_key";
    public static final String UPDATE_DISTANCE_METERS_KEY = "update_distance_key";
    public static final String STOP_DATE_KEY = "stop_date_key";
    // Managers
    private LocationManager locationManager;
    // Data members
    private ArrayList<Parameters> requirements = new ArrayList<>();
    private LocationListener locationListener;

    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged()");
                final ContentValues cv = new ContentValues(2);
                cv.put(DatabaseHelper.columnLat, location.getLatitude());
                cv.put(DatabaseHelper.columnLong, location.getLongitude());

                db.insert(DatabaseHelper.table, null, cv);
                Log.i(TAG, "Added GPS datum: Lat = " + Double.toString(location.getLatitude()) +
                           "; Long = " + Double.toString(location.getLongitude()));

                Toast.makeText(LocationService.this,
                               "Added GPS datum: Lat = " + Double.toString(location.getLatitude()) +
                               "; Long = " + Double.toString(location.getLongitude()),
                               Toast.LENGTH_SHORT).show();

                // Remove any expired studies and reset our location updates
                requirements = removeExpiredRequirements(requirements,
                                                         GregorianCalendar.getInstance().getTime());
                if (requirements.isEmpty()) {
                    stopSelf();
                    return;
                }

                requestLocationUpdates(requirements);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged()");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled()");
                final NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NotificationConstants.NOTIFICATION_ID);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled()");
                final Notification notification =
                        new NotificationCompat.Builder(LocationService.this)
                                .setContentTitle("Litmus GPS Demo")
                                .setContentText("Please re-enable GPS!")
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentIntent(PendingIntent.getActivity(LocationService.this,
                                                                            0,
                                                                            new Intent(
                                                                                    LocationService.this,
                                                                                    MainActivity.class),
                                                                            PendingIntent.FLAG_ONE_SHOT))
                                .build();
                final NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NotificationConstants.NOTIFICATION_ID, notification);
            }
        };

    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand()");
        db = new DatabaseHelper(this).getWritableDatabase();

        Log.d(TAG, "Created database.");

        // Unwrap the intent and make sure we received all data needed to run the service.
        final Bundle bundle = intent.getBundleExtra(BUNDLE_KEY);
        if (bundle == null) {
            Log.e(TAG, "onStartCommand: Intent did not have a bundle.");
            return failStart();
        }

        final long dt = bundle.getLong(UPDATE_INTERVAL_MILLIS_KEY, -1);
        if (dt == -1) {
            Log.e(TAG, "onStartCommand: Bundle did not specify an update interval.");
            return failStart();
        }

        final float dx = bundle.getFloat(UPDATE_DISTANCE_METERS_KEY, -1);
        if (dx < 0) {
            Log.e(TAG, "onStartCommand: Bundle did not specify an update distance.");
            return failStart();
        }

        final Date killDate = (Date) bundle.getSerializable(STOP_DATE_KEY);
        if (killDate == null) {
            Log.e(TAG, "onStartCommand: Bundle did not specify an end date.");
            return failStart();
        }

        // Add new study's location data requirements to our list:
        requirements.add(new Parameters(dt, dx, killDate));
        Log.d(TAG, "Added study's requirements.");

        // Ask Android for location updates corresponding to the strictest parameters:
        requestLocationUpdates(requirements);
        Log.d(TAG, "Location updates according to requirements.");

        final Notification notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Litmus GPS Demo")
                        .setContentText("We are tracking your location.")
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentIntent(PendingIntent.getActivity(this,
                                                                    0,
                                                                    new Intent(this,
                                                                               MainActivity.class),
                                                                    PendingIntent.FLAG_UPDATE_CURRENT))
                        .build();
        startForeground(NotificationConstants.FOREGROUND_NOTIFICATION_ID, notification);
        Log.d(TAG, "Started notification.");

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        } else {
            Log.i(TAG, "onDestroy: Did not have location permission.");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    private void requestLocationUpdates(final List<Parameters> req) {
        Log.d(TAG, "requestLocationUpdates()");
        if (req.isEmpty()) {
            Log.e(TAG, "requestLocationUpdates: Tried to request location updates" +
                       " with no listed requirements");
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                   findStrictestTime(req),
                                                   findStrictestDistance(req),
                                                   locationListener);
        } else {
            Log.e(TAG, "requestLocationUpdates: Did not have location permission.");
        }
    }

    private int failStart() {
        Log.d(TAG, "failStart()");
        stopSelf();
        return START_NOT_STICKY;
    }

    private long findStrictestTime(final List<Parameters> req) {
        Log.d(TAG, "findStrictestTime()");
        long dt = Long.MAX_VALUE;

        if (req.isEmpty()) {
            Log.e(TAG, "findStrictestTime: Tried to search an empty requirement list");
        }

        for (Parameters p : req) {
            if (dt > p.dt()) dt = p.dt();
        }
        Log.d(TAG, "Strictest time is " + Long.toString(dt) + " ms");
        return dt;
    }

    private float findStrictestDistance(final List<Parameters> req) {
        Log.d(TAG, "findStrictestDistance()");
        float dx = Float.MAX_VALUE;

        if (req.isEmpty()) {
            Log.e(TAG, "findStrictestDistance: Tried to search an empty requirement list");
        }

        for (Parameters p : req) {
            if (dx > p.dx()) dx = p.dx();
        }
        Log.d(TAG, "Strictest distance is " + Float.toString(dx) + " m");
        return dx;
    }

    private ArrayList<Parameters> removeExpiredRequirements(final List<Parameters> req,
                                                            final Date now) {
        Log.d(TAG, "removeExpiredRequirements()");
        ArrayList<Parameters> result = new ArrayList<>();
        for (Parameters p : req) {
            if (p.endDate().after(now)) result.add(p);
        }
        return result;
    }

    private final class Parameters {
        private long dt; // milliseconds
        private float dx; // meters
        private Date endDate;

        Parameters(long t, float x, Date d) {
            dt = t;
            dx = x;
            endDate = d;
        }

        long dt() {
            return dt;
        }

        float dx() {
            return dx;
        }

        Date endDate() {
            return endDate;
        }
    }
}

