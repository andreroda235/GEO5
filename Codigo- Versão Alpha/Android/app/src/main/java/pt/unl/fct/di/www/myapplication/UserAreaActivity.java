package pt.unl.fct.di.www.myapplication;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class UserAreaActivity extends AppCompatActivity {

    private UserLogoutTask mUserLogoutTask;
    private GetUserProfileTask mGetProfileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        DrawerLayout mDrawerLayout;
        ActionBarDrawerToggle mToggle;
        Toolbar toolbar;
        NavigationView mNavigationView;

        mDrawerLayout = findViewById(R.id.profile_nav_drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        mNavigationView = findViewById(R.id.profile_nav_page);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return parseNavigationItems(item);
            }
        });
    }

    public boolean parseNavigationItems(MenuItem item){
        int id = item.getItemId();

        switch (id) {
            case R.id.profile: {
                attemptProfilePage();
                break;
            }
            case R.id.path_manager:{
                attemptPathManager();
                break;
            }
            case R.id.settings:{
                attemptSettings();
                break;
            }
            case R.id.logout: {
                attemptLogout();
                break;
            }
        }
        return false;
    }

    private void attemptSettings() { }

    private void attemptPathManager() {
        startActivity(new Intent(UserAreaActivity.this, PathManagerActivity.class));
    }

    private void attemptProfilePage() {
        mGetProfileTask = new GetUserProfileTask();
        mGetProfileTask.execute((Void) null);
    }


    private void attemptLogout() {
        if (mUserLogoutTask == null) {
            //showProgress(true);
            SharedPreferences loginCache = getSharedPreferences("AUTHENTICATION", 0);
            mUserLogoutTask = new UserLogoutTask();
            mUserLogoutTask.execute((Void) null);
        }
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
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
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
                JSONObject token = new JSONObject(loginCache.getString("token",null));
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/logout"), token, null);
            } catch (Exception e) {
                return e.toString();
            }

        }

        @Override
        protected void onPostExecute(final String result) {
            mUserLogoutTask = null;

            if (result != null) {
                Toast.makeText(UserAreaActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(UserAreaActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUserLogoutTask = null;
            //showProgress(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetUserProfileTask extends AsyncTask<Void, Void, String> {

        GetUserProfileTask() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
            String username = settings.getString("username", null);
            String token = settings.getString("token", null).replaceAll("\"", "");
            JSONObject credential = new JSONObject();
            try {
                credential.accumulate("username", username);
                Log.i("username", username);
                Log.i("UserAreaActivity", credential.toString());
                Log.i("token", token.replaceAll("\"", ""));
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/user/get"), credential, token);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            mGetProfileTask = null;

            if (success != null){
                JSONObject token;
                Log.i("MainActivity", success);
                try {
                    token = new JSONObject(success);
                    SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString("username", token.getString("user_username"));
                    editor.putString("name", token.getString("user_name"));
                    editor.putString("email", token.getString("user_email"));
                    editor.putString("role", token.getString("user_role"));
                    editor.putString("creation_time", token.getString("user_creation_time"));
                    editor.putString("last_update", token.getString("user_last_update_time"));
                    editor.putString("street", token.getString("user_street"));
                    editor.putString("place", token.getString("user_place"));
                    editor.putString("country", token.getString("user_country"));
                    editor.putString("isActive", token.getString("active_account"));
                    editor.apply();

                    Intent intent1 = new Intent(UserAreaActivity.this, UserProfileActivity.class);
                    startActivity(intent1);
//                    finish();
                } catch (JSONException e) {
                    Log.e("Get user", e.toString());
                }
            } else {
                Log.e("Username error", "Wrong username");
            }
        }

        @Override
        protected void onCancelled() {
            mGetProfileTask = null;
        }
    }
}
