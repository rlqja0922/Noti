package com.example.notipj;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * 값들을 SharedPreferences 형태(파일 형태로 저장)
 */
public class SharedStore {
    public static String getIpPort(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("IpPort","");
    }
    public static void setIpPort(Context context, String IpPort){
        context.getSharedPreferences("MyData",0).edit().putString("IpPort",IpPort).apply();
    }


    public static String getFilter(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getString("Filter","");
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
        return sharedPreferences.getBoolean("Service",false);
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

    public static boolean getRetrofit(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData",0);
        return sharedPreferences.getBoolean("Retrofit",false);
    }
    public static void setRetrofit(Context context, boolean Retrofit){
        context.getSharedPreferences("MyData", 0).edit().putBoolean("Retrofit", Retrofit).apply();
    }
    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }

        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }

        editor.apply();
    }

    public static ArrayList getStringArrayPref(Context context, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
}
