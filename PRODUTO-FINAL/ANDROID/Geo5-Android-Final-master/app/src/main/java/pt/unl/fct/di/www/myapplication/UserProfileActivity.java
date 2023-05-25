package pt.unl.fct.di.www.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    TextView mUsernameView, mNameView, mEmailView, mStreetView, mPlaceView, mCountryView;
    TextView mAddress, mBirthday;

    private ShowUserInfoTask mShowUserInfoTask;

    private ImageView profileContainer;

    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.userProfile_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.user_profile);

        mUsernameView = findViewById(R.id.username);
        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.useremail);

        mAddress = findViewById(R.id.address);
        mBirthday = findViewById(R.id.birthday);

        profileContainer = (ImageView) findViewById(R.id.photo);
        bitmap = null;

        showUserInfo();

        FloatingActionButton mEditProfileButton = findViewById(R.id.edit);
        mEditProfileButton.setOnClickListener(v -> editUserInfo());
    }

    private void editUserInfo() {
        Intent editIntent = new Intent(this, EditUserProfileActivity.class);
        startActivity(editIntent);
        finish();
    }

    public void showUserInfo() {

        SharedPreferences cache = getSharedPreferences("AUTHENTICATION", 0);
        String username = cache.getString("username", null);

        GetProfilePicUrlTask getProfilePicUrlTask = new GetProfilePicUrlTask(username);
        getProfilePicUrlTask.execute((Void) null);

        mShowUserInfoTask = new ShowUserInfoTask();
        mShowUserInfoTask.execute((Void) null);
    }

    @SuppressLint("StaticFieldLeak")
    public class ShowUserInfoTask extends AsyncTask<Void, Void, String> {

        SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
        String mUsername = settings.getString("username", null);

        ShowUserInfoTask() { }

        @Override
        protected String doInBackground(Void... voids) {
            JSONObject username = new JSONObject();
            try{
                username.accumulate("username", mUsername);
                String token = settings.getString("token", null);
                Log.i("User", mUsername);
                Log.i("Token", token);
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/user/get"), username, token);
            } catch (Exception e) {
                Log.e("Background catch clause", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mShowUserInfoTask = null;

            SharedPreferences.Editor editor = settings.edit();
            if (result != null) {
                JSONObject token;
                try {
                    token = new JSONObject(result);

                    //Present user initials instead of photo
                    String[] username = token.getString("user_name").split(" ");
                    String nameInitials = username[0].charAt(0)+""+username[1].charAt(0);
                    TextDrawable drawable = TextDrawable.builder().buildRound(nameInitials, R.color.darkgray);
                    ImageView image = (ImageView) findViewById(R.id.photo);
                    image.setImageDrawable(drawable);

                    mUsernameView.setText(token.getString("user_username"));
                    mNameView.setText(token.getString("user_name"));
                    mEmailView.setText(token.getString("user_email"));
                    String address = token.getString("user_street")+" "+
                            token.getString("user_place")+" "+
                            token.getString("user_country")+" "+token.getString("user_zip_code");
                    mAddress.setText(address);
                    mBirthday.setText(token.getString("user_birthday"));
//                    mStreetView.setText(token.getString("user_street"));
//                    mPlaceView.setText(token.getString("user_place"));
//                    mCountryView.setText(token.getString("user_country"));

                    editor.putString("userInitials", nameInitials);
                    editor.putString("username", token.getString("user_username"));
                    editor.putString("name", token.getString("user_name"));
                    editor.putString("email", token.getString("user_email"));
                    editor.putString("role", token.getString("user_role"));
                    editor.putString("creation_time", token.getString("user_creation_time"));
                    editor.putString("last_update", token.getString("user_last_update_time"));
                    editor.putString("street", token.getString("user_street"));
                    editor.putString("place", token.getString("user_place"));
                    editor.putString("country", token.getString("user_country"));
                    editor.putString("zip_code", token.getString("user_zip_code"));
                    editor.putString("isActive", token.getString("active_account"));
                    editor.putString("birthday", token.getString("user_birthday"));
                    editor.apply();
                } catch (Exception e) {
                    Log.e("Catch clause", e.toString());
                }
            } else
                Log.e("Background", "result null");
        }

        @Override
        protected void onCancelled() {
            mShowUserInfoTask = null;
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
                bitmap = SampledPhotos.getResizedBitmap(result, 120, 120);
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
            }/*else{
                Log.d(TAG, "onPostExecute: no profile pic found");
                profileContainer.setImageDrawable(getResources().getDrawable(R.drawable.alan_grant));
            }*/
        }
    }

}
