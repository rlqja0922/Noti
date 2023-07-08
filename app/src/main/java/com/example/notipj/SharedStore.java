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


    public static String getNotiTitle(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("NotiTitle","");
    }
    public static void setNotiTitle(Context context, String NotiTitle){
        context.getSharedPreferences("MyData", 0).edit().putString("NotiTitle", NotiTitle).apply();
    }
    public static String getNotiText(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("NotiText","");
    }
    public static void setNotiText(Context context, String NotiText){
        context.getSharedPreferences("MyData", 0).edit().putString("NotiText", NotiText).apply();
    }
    public static String getNotiSubText(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("NotiSubText","");
    }
    public static void setNotiSubText(Context context, String NotiSubText){
        context.getSharedPreferences("MyData", 0).edit().putString("NotiSubText", NotiSubText).apply();
    }
    public static String getNotiPakage(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("NotiPakage","");
    }
    public static void setNotiPakage(Context context, String NotiPakage){
        context.getSharedPreferences("MyData", 0).edit().putString("NotiPakage", NotiPakage).apply();
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
