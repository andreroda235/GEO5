package pt.unl.fct.di.www.myapplication;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class UserAreaActivity extends AppCompatActivity {

    private static final String TAG = "UserAreaActivity";
    private UserLogoutTask mUserLogoutTask;
    private Bitmap bitmap;
    private ImageView profileContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        getAppLocale();

        bitmap = null;
        profileContainer = findViewById(R.id.profile_pic);

        TextView mUsername = (TextView) findViewById(R.id.profile_username);
        SharedPreferences loginCache = getSharedPreferences("AUTHENTICATION", 0);
        String username = loginCache.getString("username", null);
        mUsername.setText(username);

        GetProfilePicUrlTask getProfilePicUrlTask = new GetProfilePicUrlTask(username);
        getProfilePicUrlTask.execute((Void) null);

//        Checking session data
        if (!Session.isLogin(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    public void attemptPathManager(View mapBtn) {
        startActivity(new Intent(UserAreaActivity.this, MapActivity.class));
        finish();
    }

    public void attemptProfilePage(View profileBtn) {
        startActivity(new Intent(UserAreaActivity.this, UserProfileActivity.class));
    }


    public void attemptLogout(View LogoutBtn) {
        if (mUserLogoutTask == null)
            confirmDialog();
    }

    private void confirmDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.confirm_logout);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            mUserLogoutTask = new UserLogoutTask();
            mUserLogoutTask.execute((Void) null);
        });

        alertDialog.setNegativeButton("No", (dialog, which) -> {
            finish();
        });

        alertDialog.show();
    }

    public void setAppLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration conf = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(locale);
        }
        getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("lang_settings", 0).edit();
        editor.putString("lang", langCode);
        editor.apply();
    }

    public void getAppLocale() {
        SharedPreferences preferences = getSharedPreferences("lang_settings", 0);
        String lang = preferences.getString("lang", "");
        setAppLocale(lang);
    }

    @SuppressLint("StaticFieldLeak")
    public class UserLogoutTask extends AsyncTask<Void, Void, String> {

        UserLogoutTask() { }

        /**
         * Cancel background network operation if we do not have network connectivity
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = Objects.requireNonNull(connMgr).getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {

                //If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                SharedPreferences loginCache = getSharedPreferences("AUTHENTICATION", 0);
                JSONObject token = new JSONObject(loginCache.getString("token", null));
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/logout"), token, null);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mUserLogoutTask = null;

            if (result != null) {
                Session.setLogout(getApplicationContext());
                SharedPreferences loginCache = getSharedPreferences("AUTHENTICATION", 0);
                SharedPreferences.Editor editor = loginCache.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(UserAreaActivity.this, R.string.logout_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(UserAreaActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUserLogoutTask = null;
            //showProgress(false);
        }
    }

    public class DownloadProfilePicTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;

        DownloadProfilePicTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            return SampledPhotos.getBitmapFromURL(url);
        }

        @Override
        protected void onPostExecute(final Bitmap result) {


            if (result != null) {
                bitmap = SampledPhotos.getResizedBitmap(result, 150, 150);
                RoundedBitmapDrawable profilePic = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                profilePic.setCircular(true);
                profileContainer.setImageDrawable(profilePic);
            }
        }
    }



    public class GetProfilePicUrlTask extends AsyncTask<Void, Void, String> {
        private String username;

        GetProfilePicUrlTask(String username) {
            this.username = username;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
            SharedPreferences loginCache = getSharedPreferences("AUTHENTICATION", 0);
            String token = loginCache.getString("token", null);

                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/user/" + username + "/picture"), null, token);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {


            if (result != null) {

                Gson gson = new Gson();

                Type ImageDataType = new TypeToken<ImageData>(){}.getType();
                ImageData data = gson.fromJson(result, ImageDataType);

                String url = "https://storage.googleapis.com/apdc-geoproj.appspot.com/" + data.getFile_name();

                DownloadProfilePicTask downloadProfilePicTask = new DownloadProfilePicTask(url);
                downloadProfilePicTask.execute((Void) null);
            } else{
                Log.d(TAG, "onPostExecute: no profile pic found");
                profileContainer.setImageDrawable(getResources().getDrawable(R.drawable.alan_grant));
            }
        }
    }
}
