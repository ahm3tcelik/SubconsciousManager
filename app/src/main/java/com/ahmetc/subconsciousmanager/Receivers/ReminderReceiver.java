package com.ahmetc.subconsciousmanager.Receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import com.ahmetc.subconsciousmanager.Database.AffirmationsDao;
import com.ahmetc.subconsciousmanager.Database.DatabaseHelper;
import com.ahmetc.subconsciousmanager.R;

public class ReminderReceiver extends BroadcastReceiver {
    AlarmManager alarmMgr;
    PendingIntent pendingIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, ReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context,0,alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        long repeatTime = 100 * 60 * 60 * 24;
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),repeatTime,pendingIntent);
        NotificationCompat.Builder builder;
        final String title = "Your therapist is ready";
        DatabaseHelper dbh = new DatabaseHelper(context);
        String contentMessage = new AffirmationsDao().getAfm(dbh, context.getSharedPreferences("english",Context.MODE_PRIVATE)
        .getInt("category_id",0));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String kanalId = "kanalId";
            String kanalAd = "kanalAd";
            String kanalTanim = "kanalTanim";
            int kanalOnceligi = NotificationManager.IMPORTANCE_HIGH; // Yüksek öncelikli olarak göster
            NotificationChannel channel = notificationManager.getNotificationChannel("kanalId");
            if (channel == null) {
                channel = new NotificationChannel(kanalId, kanalAd, kanalOnceligi);
                channel.setDescription(kanalTanim);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(context, kanalId);
            builder.setContentTitle(title);
            builder.setContentText(contentMessage);
            builder.setOngoing(true);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.icon);
            builder.setVibrate(new long[] { 1000, 1000});
            builder.setColorized(true);
            builder.setColor(context.getResources().getColor(R.color.colorPrimary));
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            builder.setAutoCancel(true);

        } else {
            builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(title);
            builder.setContentText(contentMessage);
            builder.setVibrate(new long[] { 1000, 1000});
            builder.setColorized(true);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setColor(context.getResources().getColor(R.color.colorPrimary));
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            builder.setSmallIcon(R.mipmap.icon);
            builder.setAutoCancel(true);
        }
        if(contentMessage != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}