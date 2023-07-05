package com.example.notipj;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStore {
    public static String getIpPort(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("IpPort","null");
    }
    public static void setIpPort(Context context, String IpPort){
        context.getSharedPreferences("MyData",0).edit().putString("IpPort",IpPort).apply();
    }


    public static String getFilter(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("Filter","null");
    }
    public static void setFilter(Context context, String Filter){
        context.getSharedPreferences("MyData", 0).edit().putString("Filter", Filter).apply();
    }

    public static boolean getNoti(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getBoolean("Noti",true);
    }
    public static void setNoti(Context context, boolean Noti){
        context.getSharedPreferences("MyData", 0).edit().putBoolean("NotiSound", Noti).apply();
    }

    public static boolean getService(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getBoolean("Service",true);
    }
    public static void setService(Context context, boolean Service){
        context.getSharedPreferences("MyData", 0).edit().putBoolean("Service", Service).apply();
    }

    public static boolean getFirst(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getBoolean("First",false);
    }
    public static void setFirst(Context context, boolean First){
        context.getSharedPreferences("MyData", 0).edit().putBoolean("First", First).apply();
    }
}
