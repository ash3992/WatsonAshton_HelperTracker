package com.example.watsonashton_helpertracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public interface OnNewLocationListener {
        public abstract void onNewLocationReceived(String location);
    }
    static ArrayList<OnNewLocationListener> arrOnNewLocationListener = new ArrayList<OnNewLocationListener>();

    // Allows the user to set an Listener and react to the event
    public static void setOnNewLocationListener(OnNewLocationListener listener) {
        arrOnNewLocationListener.add(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        deliverNotification(context);
        OnNewLocationReceived("working");
    }

    // This function is called after the new point received
    private static void OnNewLocationReceived(String location) {
        // Check if the Listener was set, otherwise we'll get an Exception when
        // we try to call it
        if (arrOnNewLocationListener != null) {
            // Only trigger the event, when we have any listener
            for (int i = arrOnNewLocationListener.size() - 1; i >= 0; i--) {
                arrOnNewLocationListener.get(i).onNewLocationReceived(
                        location);
            }
        }
    }

    public void deliverNotification(Context context){
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setContentTitle("HelperTracker")
                .setContentText("Location text has been sent out")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}