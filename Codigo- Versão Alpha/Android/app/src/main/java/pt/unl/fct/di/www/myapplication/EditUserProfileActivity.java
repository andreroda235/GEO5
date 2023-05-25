package pt.unl.fct.di.www.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class EditUserProfileActivity extends AppCompatActivity {

    private EditUserProfileActivity mContext;
    private UserUpdateTask mUserUpdateTask;
    String mName, mPassword, mEmail, mStreet, mPlace, mCountry;
    TextView mNameView, mPasswordView, mEmailView, mStreetView, mPlaceView, mCountryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        mContext = this;

        final SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);

        mNameView = findViewById(R.id.edit_name);
        mPasswordView = findViewById(R.id.edit_password);
        mEmailView = findViewById(R.id.edit_email);
        mStreetView = findViewById(R.id.edit_street);
        mPlaceView = findViewById(R.id.edit_place);
        mCountryView = findViewById(R.id.edit_country);

        FloatingActionButton editButton = findViewById(R.id.update);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = mNameView.getText().toString();
                if (mName.equals(""))
                    mName = settings.getString("name", null);
                Log.i("Name input", mName);
                mPassword = mPasswordView.getText().toString();
                if (mPassword.equals(""))
                    mPassword = settings.getString("password", null);
                mEmail = mEmailView.getText().toString();
                if (mEmail.equals(""))
                    mEmail = settings.getString("email", null);
                mStreet = mStreetView.getText().toString();
                if (mStreet.equals(""))
                    mStreet = settings.getString("street", null);
                mPlace = mPlaceView.getText().toString();
                if (mPlace.equals(""))
                    mPlace = settings.getString("place", null);
                mCountry = mCountryView.getText().toString();
                if (mCountry.equals(""))
                    mCountry = settings.getString("country", null);
                attemptUpdate();
            }
        });
    }

    private void attemptUpdate() {
        mUserUpdateTask = new UserUpdateTask();
        mUserUpdateTask.execute((Void) null);
    }

    public class UserUpdateTask extends AsyncTask<Void, Void, String> {

        UserUpdateTask() { }

        @Override
        protected String doInBackground(Void... voids) {
            JSONObject credentials = new JSONObject();
            try {
                credentials.accumulate("name", mName);
                credentials.accumulate("password", mPassword);
                credentials.accumulate("email", mEmail);
                credentials.accumulate("street", mStreet);
                credentials.accumulate("place", mPlace);
                credentials.accumulate("country", mCountry);
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/update"), credentials, null);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mUserUpdateTask = null;

            if (result != null) {
                Toast.makeText(EditUserProfileActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditUserProfileActivity.this, UserProfileActivity.class));
            } else {
                Toast.makeText(EditUserProfileActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
            }
        }

        protected  void onCancelled() {
            mUserUpdateTask = null;
        }
    }

}
