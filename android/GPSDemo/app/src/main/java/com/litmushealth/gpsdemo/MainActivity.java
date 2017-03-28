package com.litmushealth.gpsdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final int LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLocationPermission()) {
                    startService(GpsServiceIntent());
                    Toast.makeText(MainActivity.this, "Started collecting GPS data.", Toast.LENGTH_SHORT)
                         .show();
                }
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, LocationService.class));
                Toast.makeText(MainActivity.this, "Stopped collecting GPS data.", Toast.LENGTH_SHORT)
                     .show();
            }
        });
    }

    boolean getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            // Now check again
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private void requestLocationPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new LocationPermissionDialogFragment().show(getFragmentManager(), "location_permission");
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        }
    }

    private Intent GpsServiceIntent() {
        final Bundle bundle = new Bundle();
//        bundle.putLong(LocationService.UPDATE_INTERVAL_MILLIS_KEY, 1_800_000);
        bundle.putLong(LocationService.UPDATE_INTERVAL_MILLIS_KEY, 20_000);
        bundle.putFloat(LocationService.UPDATE_DISTANCE_METERS_KEY, 0);
        final Date d = new Date();
        d.setTime(System.currentTimeMillis() + (48 * 60 * 60 * 1000));
        bundle.putSerializable(LocationService.STOP_DATE_KEY, d);

        final Intent intent = new Intent(this, LocationService.class);
        intent.putExtra(LocationService.BUNDLE_KEY, bundle);

        return intent;
    }
}
