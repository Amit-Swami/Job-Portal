package com.example.jobsrecruiter.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.jobsrecruiter.R;

public class NotificationHelper extends ContextWrapper {

    private static final String JOB_CHANNEL_ID="com.example.jobsrecruiter.AMIT";
    private static final String JOB_CHANNEL_NAME="Jobs";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel amazoneChannel=new NotificationChannel(JOB_CHANNEL_ID,
                JOB_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        amazoneChannel.enableLights(false);
        amazoneChannel.enableVibration(true);
        amazoneChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(amazoneChannel);

    }

    public NotificationManager getManager() {
        if (manager == null)
            manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getJobChannelNotification(String title, String body, PendingIntent contentIntent,
                                                          Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),JOB_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
