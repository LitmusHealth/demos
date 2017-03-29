package com.litmushealth.gpsdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * @author Rob Agnese
 * (c) 2017 Litmus Health
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // At boot, we just nag the user to start the app.
        final Notification notification =
                new NotificationCompat.Builder(context)
                        .setContentTitle("Litmus GPS Demo")
                        .setContentText("Start the tracking app! You know you want to...")
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentIntent(PendingIntent.getActivity(context,
                                                                    0,
                                                                    new Intent(
                                                                            context,
                                                                            MainActivity.class),
                                                                    PendingIntent.FLAG_UPDATE_CURRENT))
                        .build();

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationConstants.NOTIFICATION_ID, notification);
    }
}
