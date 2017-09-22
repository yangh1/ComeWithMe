package edu.rosehulman.ComeWithMe.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import edu.rosehulman.ComeWithMe.Constants;
import edu.rosehulman.ComeWithMe.Model.Event;


public class SharedPreferencesUtils {

    public static String getCurrentUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.UID_KEY, "");
    }

    public static void removeCurrentUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.UID_KEY);
        editor.apply();
    }

    public static void setCurrentUser(Context context, String uid) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.UID_KEY, uid);
        editor.commit();
    }

    public static void settUserUsername(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.USER_USERNAME,username);
        editor.commit();
    }

    public static void settUserEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.USER_EMAIL,email);
        editor.commit();
    }

    public static String getUserUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.USER_USERNAME, "");
    }

    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.USER_EMAIL, "");
    }

    public static void setEventKey(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.EVENT_KEY,key);
        editor.commit();
    }

    public static String getEventKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.EVENT_KEY, "");
    }

    public static void setEvent(Context context,Event event){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.EVENT_KEY,event.getKey());
        editor.putString(Constants.EVENT_TITLE,event.getTitle());
        editor.putString(Constants.EVENT_HOST,event.getHost());
        editor.putString(Constants.EVENT_DATE,event.getDate());
        editor.putString(Constants.EVENT_TIME,event.getTime());
        editor.putString(Constants.EVENT_LOCATION,event.getLocation());
        editor.putString(Constants.EVENT_EVENTKEY,event.getEventKey());
        editor.commit();
    }

    public static Event getEvent(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Event e = new Event(prefs.getString(Constants.EVENT_TITLE,""),
            prefs.getString(Constants.EVENT_HOST, ""),
            prefs.getString(Constants.EVENT_DATE, ""),
            prefs.getString(Constants.EVENT_TIME,""),
            prefs.getString(Constants.EVENT_LOCATION,""),
            prefs.getString(Constants.EVENT_EVENTKEY,""));
        e.setKey(prefs.getString(Constants.EVENT_KEY, ""));
        return e;
    }

}
