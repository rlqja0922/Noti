package com.example.notipj;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Foreground extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private RetrofitNoti retrofitInterface;
    private int id = 45;
    static boolean serviceStatus;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // startService() 혹은  startForegroundService() 를 호출하여 서비스를 시작할때마다 호출됨
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.noti)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(1,notification);
                startForeground(1, notification);
        //do heavy work on a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.e("Service", "서비스가 실행 중입니다...");
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }
    @Nullable
    public void retrofitNoti(){
        String url = SharedStore.getIpPort(getApplicationContext());
        try {
            retrofit2.Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitInterface = retrofit.create(RetrofitNoti.class);
            NotificationData notificationdata = new NotificationData("dd","tt","name");
            Call<NotificationData> call = retrofitInterface.getnotification(notificationdata);
            call.enqueue(new Callback<NotificationData>() {
                @Override
                public void onResponse(Call<NotificationData> call, Response<NotificationData> response) {
                    if (response.isSuccessful()) {
                        NotificationData notificationData = response.body();
                        boolean status = notificationData.getStatus();

                    }

                }

                @Override
                public void onFailure(Call<NotificationData> call, Throwable t) {

                    stopSelf();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
//    }
//
//    @Override
//    public void onNotificationPosted(StatusBarNotification sbn) {
//        super.onNotificationPosted(sbn);
//        String filter = SharedStore.getFilter(getApplicationContext());
//        sbn.getNotification();
//        Log.d("Notifilter",sbn.getNotification().toString());
//
//        retrofitNoti();
//    }
}