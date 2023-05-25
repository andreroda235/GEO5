package pt.unl.fct.di.www.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {

    private UserProfileActivity mContext;
    private EditUserProfileActivity mEditProfileTask;

    String mUsername, mName, mEmail, mStreet, mPlace, mCountry;
    TextView mUsernameView, mNameView, mEmailView, mStreetView, mPlaceView, mCountryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
        mUsername = settings.getString("username", null);
        mName = settings.getString("name", null);
        mEmail = settings.getString("email", null);
        mStreet = settings.getString("street", null);
        mPlace = settings.getString("place", null);
        mCountry = settings.getString("country", null);

        mUsernameView = findViewById(R.id.username);
        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);
        mStreetView = findViewById(R.id.street);
        mPlaceView = findViewById(R.id.place);
        mCountryView = findViewById(R.id.country);

        mUsernameView.setText(mUsername);
        mNameView.setText(mName);
        mEmailView.setText(mEmail);
        mStreetView.setText(mStreet);
        mPlaceView.setText(mPlace);
        mCountryView.setText(mCountry);

        FloatingActionButton mEditProfileButton = findViewById(R.id.edit);
        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(mContext, EditUserProfileActivity.class);
                startActivity(editIntent);
//                finish();
            }
        });
    }

}
