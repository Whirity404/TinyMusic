package com.minesms.music;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationKeepService extends Service {
    private static final String CHANNEL_ID = "Foreground";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TMusic")
                .setContentText("TMusic Notification Module")
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true); // 设置为常驻通知

        startForeground(1, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果服务被杀死，系统会尝试重新创建服务
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
