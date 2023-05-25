package pt.unl.fct.di.www.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    public static final String KEYLOGIN = "loginkey";

    public static boolean isLogin(Context context) {
        return Boolean.parseBoolean(getSession(context, KEYLOGIN));
    }

    public static void setLogin(Context context) {
        saveSession(context, "true", KEYLOGIN);
    }

    public static void setLogout(Context context) {
        saveSession(context, "false", KEYLOGIN);
    }

    public static void saveSession(Context context, String value, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSession(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", 0);
        return sharedPreferences.getString(key, "false");
    }
}
