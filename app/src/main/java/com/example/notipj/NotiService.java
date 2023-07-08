package com.example.notipj;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NotiService extends NotificationListenerService {

    private RetrofitNoti retrofitInterface;
    private String title,text,subtext,packageName;
    public NotiService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("NotificationListener", "onDestroy()");
    }
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d("Notiremove",sbn.getNotification().toString());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String filter = SharedStore.getFilter(getApplicationContext());
        sbn.getNotification();
        Notification notification = sbn.getNotification();
        Bundle extras = sbn.getNotification().extras;

        title = extras.getString(Notification.EXTRA_TITLE);
        packageName = sbn.getPackageName();
        text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
        if (extras.getCharSequence(Notification.EXTRA_SUB_TEXT)!=null){
            subtext = extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString();
        }
        Log.d("Notifilter",sbn.getNotification().toString());
        if (packageName!=getPackageName()){
            if (SharedStore.getService(getApplicationContext())){
                Foreground.updateNoit();
//        retrofitNoti();
            }
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
            NotificationData notificationdata = new NotificationData(title,text,packageName);
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

                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}