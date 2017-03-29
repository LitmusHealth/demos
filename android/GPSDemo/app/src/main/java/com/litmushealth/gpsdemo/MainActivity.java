package com.litmushealth.gpsdemo;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

/**
 * @author Rob Agnese
 * (c) 2017 Litmus Health
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                                                      Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Already have permission");
                    startService(GpsServiceIntent());
                    Toast.makeText(MainActivity.this,
                                   "Started collecting GPS data.",
                                   Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Log.d(TAG, "Don't have permission");
                    requestLocationPermission();
                }
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, LocationService.class));
                Toast.makeText(MainActivity.this,
                               "Stopped collecting GPS data.",
                               Toast.LENGTH_SHORT)
                     .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // Clear any notification
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationConstants.NOTIFICATION_ID);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult()");
        // This gets called when the user responds to the permission request

        switch (requestCode) {
            case LOCATION_PERMISSION: {
                Log.d(TAG, "LOCATION_PERMISSION");
                if (allPermissionsGranted(grantResults)) {
                    Log.d(TAG, "all permissions granted");
                    startService(GpsServiceIntent());
                    Toast.makeText(MainActivity.this,
                                   "Started collecting GPS data.",
                                   Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Log.d(TAG, "some permissions denied");
                    Toast.makeText(MainActivity.this,
                                   "Permission denied.",
                                   Toast.LENGTH_SHORT)
                         .show();
                }
                break;
            }
        }
    }

    private boolean allPermissionsGranted(@NonNull final int[] grantResults) {
        Log.d(TAG, "allPermissionsGranted()");
        for (final int g : grantResults) {
            if (g != PackageManager.PERMISSION_GRANTED) return false;
            Log.d(TAG, "permission denied!");
        }

        return true;
    }

    private void requestLocationPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Make async?
            new LocationPermissionDialogFragment().show(getFragmentManager(),
                                                        "location_permission");
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                              LOCATION_PERMISSION);
        }
    }

    private Intent GpsServiceIntent() {
        final Bundle bundle = new Bundle();
        bundle.putLong(LocationService.UPDATE_INTERVAL_MILLIS_KEY, 1_800_000);
        bundle.putFloat(LocationService.UPDATE_DISTANCE_METERS_KEY, 0);
        final Date d = new Date();
        d.setTime(System.currentTimeMillis() + (48 * 60 * 60 * 1000));
        bundle.putSerializable(LocationService.STOP_DATE_KEY, d);

        final Intent intent = new Intent(this, LocationService.class);
        intent.putExtra(LocationService.BUNDLE_KEY, bundle);

        return intent;
    }
}
