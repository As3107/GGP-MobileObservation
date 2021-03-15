package com.ggp.ai_ggpmobile.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ggp.ai_ggpmobile.R;

public class MyNotificationManager extends Service {

    public static final int ID_SMALL_NOTIFICATION = 235;

    public Context mCtx;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void showSmallNotification(String title, String message, Intent intent) {
        String CHANNEL_ID = "IdChannel";
        String CHANNEL_NAME = "NotifChannel";
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationManager notification = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(mCtx, android.R.color.transparent))
//                setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(resultPendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSound(alarmSound);

        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            builder.setChannelId(CHANNEL_ID);
            if (notification != null) {
                notification.createNotificationChannel(channel);
            }
        }
        Notification notif = builder.build();
        /*startForeground(ID_SMALL_NOTIFICATION, notif);
        startService(intent);
        assert notification != null;
        notification.notify(ID_SMALL_NOTIFICATION, notif);*/

        if (notification != null) {
            notification.notify(ID_SMALL_NOTIFICATION, builder.build());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // create and display a notification
        Notification notificationPopup = new Notification.Builder(this)
                .setContentTitle("Alarm is ON!")
                .setContentText("Click here")
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationPopup);

        return super.onStartCommand(intent, flags, startId);
    }

}
