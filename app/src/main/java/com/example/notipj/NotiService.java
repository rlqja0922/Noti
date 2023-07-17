package com.example.notipj;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NotiService extends NotificationListenerService {

    private RetrofitNoti retrofitInterface;
    private String title,text,subtext,packageName;
    private Context context;
    private Intent msg = new Intent("Msg");
    public NotiService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        subtext = "서브 텍스트가 없습니다.";
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

        if (SharedStore.getService(context)){
            String filter = SharedStore.getFilter(context);
            sbn.getNotification();
            Notification notification = sbn.getNotification();
            Bundle extras = sbn.getNotification().extras;

            title = extras.getString(Notification.EXTRA_TITLE);
            packageName = sbn.getPackageName();

            if (extras.getCharSequence(Notification.EXTRA_TEXT)!=null){
                text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            }else {
                text = "텍스트가 없습니다.";
            }

            if (extras.getCharSequence(Notification.EXTRA_SUB_TEXT)!=null){
                subtext = extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString();
            }else {
                subtext = "서브 텍스트가 없습니다.";
            }
            Log.d("Notifilter",sbn.getNotification().toString());
            if (packageName!=getPackageName()){
                if (SharedStore.getFilter(context).length()>0 ){
                    if (title.contains(SharedStore.getFilter(context)) || text.contains(SharedStore.getFilter(context)) || subtext.contains(SharedStore.getFilter(context)) ){
                        SharedStore.setNotiText(context,text);
                        SharedStore.setNotiSubText(context,subtext);
                        SharedStore.setNotiPakage(context,packageName);
                        SharedStore.setNotiTitle(context,title);
                        Foreground.updateNoit();
                    }
                }else if (SharedStore.getFilter(context).equals("")){
                    SharedStore.setNotiText(context,text);
                    SharedStore.setNotiSubText(context,subtext);
                    SharedStore.setNotiPakage(context,packageName);
                    SharedStore.setNotiTitle(context,title);
                    Foreground.updateNoit();
                }

                msg.putExtra("subtext", subtext);
                msg.putExtra("title", title);
                msg.putExtra("text", text);
                msg.putExtra("tyoe","noti");

                retrofitNoti();


                LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
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

                        SharedStore.setRetrofit(context,true);

                        msg.putExtra("tyoe","url");
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