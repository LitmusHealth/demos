package com.litmushealth.gpsdemo;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

/**
 * @author Rob Agnese
 * (c) 2017 Litmus Health
 */

public class LocationPermissionDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("We REALLY want your location. kthx.")
                .setPositiveButton("Request Again",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           ActivityCompat.requestPermissions(getActivity(),
                                                                             new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                                                                             MainActivity.LOCATION_PERMISSION);
                                       }
                                   })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
					                                dialog.cancel();
                                                }
                })
                .create();
    }
}
