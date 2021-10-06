package util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

import houston.david.hikingapp.R;

public class NotifactionHelper extends ContextWrapper {

    public static final int NOTIFICATION_REQ_CODE = 267;
    private String CHANNEL_NAME = "High Priority";
    private String CHANNEL_ID = "houston.david.hikingapp" + CHANNEL_NAME;


    /**
     * Checks if android version is greater than version O
     * if so create notifaction channels
     * @param base
     */
    public NotifactionHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationsChannels();
        }
    }

    /**
     * `Create notification channels to send notifications.
     */
    private void notificationsChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("Geofence Channel");
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    /**
     * When called, this method sends a notification with the
     * title and body and classes passed in as parameters.
     * @param title
     * @param body
     * @param activity
     */
    public void sendNotification(String title, String body, Class activity) {

        Intent intent = new Intent(this, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_baseline_map_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().setSummaryText("Summary").setBigContentTitle(title).setSummaryText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);


    }
}
