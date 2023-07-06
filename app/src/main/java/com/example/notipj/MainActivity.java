package com.example.notipj;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity  {

    private EditText url_et, filter_et;
    private TextView apply_tv,apply_tv2,textView_noti,textView_noti2,textView_api,textView_api2;
    private RetrofitNoti retrofitInterface;
    private String apply_st;
    protected Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url_et = findViewById(R.id.url_et);
        filter_et = findViewById(R.id.filter_et);
        apply_tv = findViewById(R.id.apply_tv);
        apply_tv2 = findViewById(R.id.apply_tv2);
        textView_noti = findViewById(R.id.textView_noti);
        textView_noti2 = findViewById(R.id.textView_noti2);
        textView_api = findViewById(R.id.textView_api);
        textView_api2 = findViewById(R.id.textView_api2);
        context = getApplicationContext();
        url_et.setText(SharedStore.getIpPort(context));
        apply_st = url_et.getText().toString();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()){
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ android.Manifest.permission.POST_NOTIFICATIONS},101);
                }   }
        }else{

        }
        apply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), Foreground.class);// MyBackgroundService 를 실행하는 인텐트 생성
                stopService(serviceIntent);
                SharedStore.setIpPort(getApplicationContext(),url_et.getText().toString());
//                urlOk();
                service();
            }
        });
        apply_tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedStore.setFilter(getApplicationContext(),filter_et.getText().toString());
            }
        });
    }


    public void service(){
        Intent serviceIntent = new Intent(this, Foreground.class);// MyBackgroundService 를 실행하는 인텐트 생성

        if (SharedStore.getService(context)){
            stopService(serviceIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 현재 안드로이드 버전 점검
            startForegroundService(serviceIntent);// 서비스 인텐트를 전달한 foregroundService 시작 메서드 실행
        }else {
            startService(serviceIntent);// 서비스 인텐트를 전달한 서비스 시작 메서드 실행
        }
    }

    @Nullable
    public void urlOk(){
        String url = SharedStore.getIpPort(getApplicationContext());
        try {
            retrofit2.Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitInterface = retrofit.create(RetrofitNoti.class);
            NotificationData notificationdata = new NotificationData("test","test","nametest");
            Call<NotificationData> call = retrofitInterface.getnotification(notificationdata);
            call.enqueue(new Callback<NotificationData>() {
                @Override
                public void onResponse(Call<NotificationData> call, Response<NotificationData> response) {
                    if (response.isSuccessful()) {
                        NotificationData notificationData = response.body();
                        boolean status = notificationData.getStatus();


                        SharedStore.setFirst(context,false);
                        SharedStore.setService(context,true);
                        SharedStore.setIpPort(context,apply_st);
                        service();
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