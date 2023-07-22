package com.example.notipj;

import static android.net.wifi.WifiConfiguration.Status.strings;
import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity  {

    private EditText url_et, filter_et;
    private TextView apply_tv,apply_tv2,textView_noti,textView_noti2,textView_api,textView_api2,filter_go;
    private RetrofitNoti retrofitInterface;
    private String apply_st;
    protected Context context;
    private ArrayList<String> filter_list;
    private String filter_key = "filterkey";

    private ListFragment fragment;

    private FrameLayout fragment_container;
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
        filter_go = findViewById(R.id.filter_go);
        fragment_container = findViewById(R.id.fragment_container);
        context = getApplicationContext();
        fragment = new ListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction =  fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment).commitAllowingStateLoss();
        fragment_container.setVisibility(View.GONE);
        filter_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new ListFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction =  fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment).commitAllowingStateLoss();
                fragment_container.setVisibility(View.VISIBLE);
            }
        });
        url_et.setText(SharedStore.getIpPort(context));
        apply_st = url_et.getText().toString();
        filter_list = SharedStore.getStringArrayPref(context,filter_key);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()){
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ android.Manifest.permission.POST_NOTIFICATIONS},101);
                }
            }
        }else{

        }
        if (!SharedStore.getService(context)){
            SharedStore.setRetrofit(context,false);
        }
        if (SharedStore.getRetrofit(context)){
            textView_api.setText(getString(R.string.api_notext1));
            textView_api2.setText(getString(R.string.api_notext2));
        }


        //Notification 허락 여부 확인
        boolean isPermissionAllowed = isNotiPermissionAllowed();

        if(!isPermissionAllowed) {
            Toast.makeText(context,"알림 접근 허용 권한을 체크해주세요.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
        apply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (url_et.getText().toString().length()==0){
                    Toast.makeText(context,"URL을 입력해주세요.",Toast.LENGTH_LONG).show();
                }else {
                    SharedStore.setIpPort(context,url_et.getText().toString());
                    show();
                }
            }
        });
        apply_tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show2();
//                SharedStore.setFilter(getApplicationContext(),filter_et.getText().toString());
            }
        });
    }

    private boolean isNotiPermissionAllowed() {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        //Notification권한이 있는 경우
        if(notiListenerSet.contains(getPackageName())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
    public void service(){
        if (filter_et.getText().toString().length()>0){
            SharedStore.setFilter(context,filter_et.getText().toString());
        }else {
            SharedStore.setFilter(context,"");
        }
        Intent serviceIntent = new Intent(this, Foreground.class);// MyBackgroundService 를 실행하는 인텐트 생성

        if (SharedStore.getService(context)){
            stopService(serviceIntent);
        }

        //Notification-카카오톡 메세지 수신 대기
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 현재 안드로이드 버전 점검
            startForegroundService(serviceIntent);// 서비스 인텐트를 전달한 foregroundService 시작 메서드 실행
        }else {
            startService(serviceIntent);// 서비스 인텐트를 전달한 서비스 시작 메서드 실행
        }
        SharedStore.setIpPort(getApplicationContext(),url_et.getText().toString());

        Toast.makeText(context,"서비스가 시작되었습니다.",Toast.LENGTH_LONG).show();
    }
    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("URL Change");
        builder.setMessage("Are you sure change?");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                urlOk();
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
    public void show2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter add");
        builder.setMessage("Are you sure add filter?");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filter_list.add(filter_et.getText().toString());
                SharedStore.setStringArrayPref(context,filter_key,filter_list);
                filter_et.setText("");
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
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
                    Log.e("urlerror",t.getMessage());
                    Toast.makeText(context,"Check the server is running.",Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(context,"Check the server is running.",Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }
    }
    //noti listener 감지시 동작할 브로트캐스트 리시버
    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String subtext = intent.getStringExtra("subtext");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            String type = intent.getStringExtra("type");
            if (type.equals("url")){
                textView_api.setText(title);
                textView_api2.setText(text);
            }else {
                textView_api.setText(R.string.api_notext1);
                textView_api2.setText(R.string.api_notext2);
            }
            textView_noti.setText(title);
            textView_noti2.setText(text);
        }

    };
}