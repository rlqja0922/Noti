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
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity  {

    private EditText url_et, filter_et;
    private TextView apply_tv,apply_tv2,textView_noti,textView_noti2,textView_api,textView_api2,filter_go,textView_noti_package,textView_noti_time,textView_api_package,textView_api_time;
    private RetrofitNoti retrofitInterface;
    private String apply_st;
    protected Context context;
    private ArrayList<String> filter_list;
    private String filter_key = "filterkey";

    private ListFragment fragment;

    private FrameLayout fragment_container;

    // 변수 초기화
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
        textView_noti_package = findViewById(R.id.textView_noti_package);
        textView_noti_time = findViewById(R.id.textView_noti_time);
        textView_api_package = findViewById(R.id.textView_api_package);
        textView_api_time = findViewById(R.id.textView_api_time);
        context = getApplicationContext();
        //리스트 페이지를 액티비티가아닌 프레그먼트를 사용하기위한 프래그먼트 초기화 구문
        fragment = new ListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction =  fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        getSupportActionBar().hide();
        fragment_container.setVisibility(View.GONE);
        Timer serviceTimer = new Timer();
        TimerTask serviceTimerTask = new TimerTask() {
            @Override
            public void run() {
                service(); //앱 시작시 자동으로 서비스 시작
            }
        };
        serviceTimer.schedule(serviceTimerTask,1000);

        //필터> 버튼을 누를시 실행되는 코드 프래그번트를 초기화 하며 레이아웃을 나타나게 해줌
        filter_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new ListFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction =  fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
                fragment_container.setVisibility(View.VISIBLE); //레이아웃 나타내는 코드
            }
        });
        url_et.setText(SharedStore.getIpPort(context));
        apply_st = url_et.getText().toString();
        filter_list = SharedStore.getStringArrayPref(context,filter_key);

        //권한 확인을 위한 코드 안드로이드 버전에 따른 권한 부여(노티 띄우는 권한)
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


        //권한 확인을 위한 코드 안드로이드 버전에 따른 권한 부여(노티 정보 읽어오는 권한)
        boolean isPermissionAllowed = isNotiPermissionAllowed();

        if(!isPermissionAllowed) {
            Toast.makeText(context,"알림 접근 허용 권한을 체크해주세요.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
         /**
         url apply 버튼 누를시 동작
        입력창이 비어있을시 토스트 메시지를 출력 , 입력값이 있을경우 해당 url을 저장, 통신을 이용하여 사용가능한 url인지 확인
          **/
        apply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (url_et.getText().toString().length()==0){
                    Toast.makeText(context,"URL을 입력해주세요.",Toast.LENGTH_LONG).show();
                }else {
                    show(); //얼럿창 띄우는 코드
                }
            }
        });
        //필터 add누를시 나오는 코드
        apply_tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_et.getText().toString().length()>0)
                    show2();// 얼럿창 띄우는 코드
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

    //서비스 동작 코드
    public void service(){

        Intent serviceIntent = new Intent(context, Foreground.class);// MyBackgroundService 를 실행하는 인텐트 생성

        if (SharedStore.getService(context)){
            stopService(serviceIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isServiceRunningCheck()){
                        context.startForegroundService(serviceIntent);
                    }
                }
            },100);

        } else {
            if(isServiceRunningCheck()){
                context.startService(serviceIntent);
            }
        }
        //Notification-메세지 수신 대기
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(Foreground.class).addTag ( "BACKUP_WORKER_TAG" ).build ();
//            WorkManager.getInstance ( context ).enqueue ( request );
//        } else
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// 현재 안드로이드 버전 점검
//            context.startForegroundService ( serviceIntent );// 서비스 인텐트를 전달한 서비스 시작 메서드 실행
//        } else {
//            context.startService ( serviceIntent );// 서비스 인텐트를 전달한 서비스 시작 메서드 실행
//        }

        SharedStore.setFirst(context,false);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Toast.makeText(context,"서비스가 시작되었습니다.",Toast.LENGTH_LONG).show();
            }
        }, 0);
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
    //입력한 url이 맞는 url인지 확인용 api
    @Nullable
    public void urlOk(){
        String url = url_et.getText().toString();
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
                        SharedStore.setIpPort(context,url_et.getText().toString());
                        NotificationData notificationData = response.body();
                        boolean status = notificationData.getStatus();
                        SharedStore.setIpPort(context,apply_st);
                        SharedStore.setRetrofit(context,true);
                    }

                }

                @Override
                public void onFailure(Call<NotificationData> call, Throwable t) {
                    Log.e("urlerror",t.getMessage());
                    textView_api.setText(R.string.api_notext1);
                    textView_api2.setText(R.string.api_notext2);
                    textView_api_package.setText(R.string.api_notext1);
                    textView_api_time.setText("xx : xx");
                    Toast.makeText(context,R.string.api_notext2,Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(context,R.string.api_notext2,Toast.LENGTH_LONG).show();
            textView_api.setText(R.string.api_notext1);
            textView_api2.setText(R.string.api_notext2);
            textView_api_package.setText(R.string.api_notext1);
            textView_api_time.setText("xx : xx");
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
            String time = intent.getStringExtra("time");
            String packagename = intent.getStringExtra("packagename");
            //type값을 이용해서 url이 아닐시 lastnoti의 값을, url일 경우 api request의 값을 변경해준다.
            if (type.equals("url")){
                textView_api.setText(title);
                textView_api2.setText(text);
                textView_api_package.setText(packagename);
                textView_api_time.setText(time);
            }else {
                textView_noti.setText(title);
                textView_noti2.setText(text);
                textView_noti_package.setText(packagename);
                textView_noti_time.setText(time);
            }
        }

    };
    public interface onBackPressedListener{
        void onBackPressed();
    }
    @Override
    public void onBackPressed() {
        if (fragment_container.getVisibility()!=View.GONE){
            fragment_container.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (!"com.example.notipj.Foreground".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}