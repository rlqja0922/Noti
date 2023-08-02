package com.example.notipj;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NotiService extends NotificationListenerService {

    private RetrofitNoti retrofitInterface;
    private String title,text,subtext,packageName,time;
    private Context context;
    private Intent msg;

    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("a hh:mm");
    public NotiService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //가끔 나는 텍스트 null오류를 없애기위해 변수 초기화
        subtext = "서브 텍스트가 없습니다.";
        text = "텍스트";
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
    public String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
//com.example.notipj ,com.android.systemui
        if (!sbn.getPackageName().equals("com.example.notipj")&&!sbn.getPackageName().equals("com.android.systemui")&&!sbn.getPackageName().contains("com.android.")){

            if (SharedStore.getService(context)){
                msg = new Intent("Msg");
                sbn.getNotification();
                Notification notification = sbn.getNotification();
                Bundle extras = sbn.getNotification().extras;

                title = extras.getString(Notification.EXTRA_TITLE);
                packageName = sbn.getPackageName();
                time = getTime();

                if (extras.getCharSequence(Notification.EXTRA_TITLE)!=null){
                    title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                }else {
                    title = "Title이 존재하지 않습니다.";
                }

                if (extras.getCharSequence(Notification.EXTRA_TEXT)!=null){
                    text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                }else {
                    text = "Text가 없습니다.";
                }

                if (extras.getCharSequence(Notification.EXTRA_SUB_TEXT)!=null){
                    subtext = extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString();
                }else {
                    subtext = "Sub Text가 없습니다.";
                }
                Log.d("Notifilter",sbn.getNotification().toString());

                SharedStore.setNotiText(context,text);
                SharedStore.setNotiSubText(context,subtext);
                SharedStore.setNotiPakage(context,packageName);
                SharedStore.setNotiTitle(context,title);
                //브로드캐스트 리시버에 담을 값을 입력
                msg.putExtra("subtext", subtext);
                msg.putExtra("title", title);
                msg.putExtra("text", text);
                msg.putExtra("packagename",packageName);
                msg.putExtra("time",time);
                msg.putExtra("type","noti");

                broadcast();

                //url apply 버튼을 누른 후에만 접근되는 코드
                if (SharedStore.getRetrofit(context)){
                    ArrayList<String> list = SharedStore.getStringArrayPref(context,"filterkey");
                    if (list.size()>0){
                        for (int i = 0;i<list.size(); i++){
                            if(!title.equals(null)&&!text.equals(null)&&!title.equals(null)){
                                if (title.contains(list.get(i)) || text.contains(list.get(i)) || packageName.contains(list.get(i))){
                                    SharedStore.setNotiText(context,text);
                                    SharedStore.setNotiPakage(context,packageName);
                                    SharedStore.setNotiTitle(context,title);
                                    msg.putExtra("title", title);
                                    msg.putExtra("text", text);
                                    msg.putExtra("packagename",packageName);
                                    msg.putExtra("time",time);
                                    msg.putExtra("type","url");
                                    retrofitNoti();
                                    break;
                                }
                            }
                        }
                    }

                }

            }
        }

    }
    //MainActivity 브로드케스트 리시버를 실행시키는 코드
    public void broadcast(){
        LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
    }

// 노티값을 이용한 api통신
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
                        broadcast();
                    }
                }
                @Override
                public void onFailure(Call<NotificationData> call, Throwable t) {
                    SharedStore.setRetrofit(context,false);
                    Toast.makeText(context,R.string.api_notext2,Toast.LENGTH_LONG).show();

                    msg.putExtra("title", R.string.api_notext1);
                    msg.putExtra("text", R.string.api_notext2);
                    msg.putExtra("packagename",R.string.api_notext1);
                    msg.putExtra("time","xx : xx");
                    msg.putExtra("type","url");
                    broadcast();
                }
            });

        } catch (Exception e) {
            SharedStore.setRetrofit(context,false);
            Toast.makeText(context,R.string.api_notext2,Toast.LENGTH_LONG).show();
            msg.putExtra("title", R.string.api_notext1);
            msg.putExtra("text", R.string.api_notext2);
            msg.putExtra("packagename",R.string.api_notext1);
            msg.putExtra("time","xx : xx");
            msg.putExtra("type","url");
            broadcast();
        }
    }


}