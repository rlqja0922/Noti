package com.example.notipj;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//NotificationListenerService
public class Foreground extends Service  {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static int id = 45;
    private static NotificationManager mNotificationManager;
    private NotificationCompat.Builder notifyBuilder;
    private static PendingIntent pendingIntent;
    private String title,subtext,text,packageName;
    private static Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Notification Builder를 만드는 메소드
    private static NotificationCompat.Builder getNotificationBuilder() {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(SharedStore.getNotiTitle(context))
                .setContentText(SharedStore.getNotiText(context))
                .setOngoing(true)
                .setSmallIcon(R.drawable.noti);
        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    // startService() 혹은  startForegroundService() 를 호출하여 서비스를 시작할때마다 호출됨
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedStore.setService(getApplicationContext(),true);
        title = "Foreground Service";
        subtext = "서비스 실행중입니다.";
        context = getApplicationContext();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        // Builder 생성
        notifyBuilder = getNotificationBuilder();
        notifyBuilder.setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH);
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(id,notifyBuilder.build());

//        //do heavy work on a background thread 서비스가 잘돌아가는지 확인용 쓰레드
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    Log.e("Service", "서비스가 실행 중입니다...");
//                    try {
//                        Thread.sleep(2000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }).start();
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void createNotificationChannel() {
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID
                    ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
    public static void updateNoit(){

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(id,notifyBuilder.build());
    }

}