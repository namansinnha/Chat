package com.developer.gkweb.knowlocation.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;

public class OreoNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "com.developer.hex.chat";
    private static final String CHANNEL_NAME = "ChatApp";

    private NotificationManager notificationManager;

    public OreoNotification(Context base) {
        super (base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            createChannel();

        }

    }

    @TargetApi (Build.VERSION_CODES.O)
    private void createChannel () {

        NotificationChannel channel = new NotificationChannel (CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);

        channel.enableLights (false);
        channel.enableVibration (true);
        channel.setLockscreenVisibility (Notification.VISIBILITY_PRIVATE);

        getNotificationManager ().createNotificationChannel (channel);

    }

    public NotificationManager getNotificationManager() {

        if (notificationManager == null) {

            notificationManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);

        }

        return notificationManager;

    }

    @TargetApi (Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification (String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon) {

        return new Notification.Builder (getApplicationContext (), CHANNEL_ID)
                .setContentIntent (pendingIntent)
                .setContentText (body)
                .setSmallIcon(Integer.parseInt (icon))
                .setSound (soundUri)
                .setAutoCancel (true);

    }


}
