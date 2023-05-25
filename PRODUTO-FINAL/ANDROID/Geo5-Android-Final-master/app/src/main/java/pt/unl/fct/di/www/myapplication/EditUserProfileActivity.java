package pt.unl.fct.di.www.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;
import java.util.Objects;

public class EditUserProfileActivity extends AppCompatActivity {

    private View mProgressView, mUpdateFormView;
    private TextView mNameView, mPasswordView, mEmailView, mStreetView, mPlaceView, mCountryView;
    private TextView mZipcodeView, mBirthdayView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private TextView mDeleteView;
    private String mName, mPassword, mEmail, mStreet, mPlace, mCountry, mZipcode, mBirthday;
    private UserUpdateTask mUserUpdateTask = null;
    private DeleteUserTask mDeleteUserTask = null;
    private SharedPreferences settings;
    private final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        Toolbar toolbar = findViewById(R.id.editUser_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = getSharedPreferences("AUTHENTICATION", 0);
        String nameInitials = settings.getString("userInitials", null);
        TextDrawable drawable = TextDrawable.builder().buildRound(nameInitials, R.color.darkgray);
        ImageView image = (ImageView) findViewById(R.id.edit_photo);
        image.setImageDrawable(drawable);

        mNameView = findViewById(R.id.edit_name);
        mPasswordView = findViewById(R.id.edit_password);
        mEmailView = findViewById(R.id.edit_email);
        mStreetView = findViewById(R.id.edit_street);
        mPlaceView = findViewById(R.id.edit_place);
        mCountryView = findViewById(R.id.edit_country);
        mZipcodeView = findViewById(R.id.edit_zip_code);
        mBirthdayView = findViewById(R.id.edit_birthday);

        mBirthdayView.setOnClickListener(v -> {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditUserProfileActivity.this,
                    mDateSetListener, year, month, day);
            datePickerDialog.show();
        });

        mDateSetListener = (view, year, month, dayOfMonth) -> {
            //TODO
            month+=1;
            String date = dayOfMonth+"/"+month+"/"+year;
            mBirthdayView.setText(date);
        };

        mDeleteView = findViewById(R.id.delete_name);

        FloatingActionButton updateButton = findViewById(R.id.update);
        updateButton.setOnClickListener(v -> attemptUpdate());

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            mDeleteView.setVisibility(View.VISIBLE);
            attemptDelete();
        });

        mUpdateFormView = findViewById(R.id.update_form);
        mProgressView = findViewById(R.id.update_progress);
    }

    /**
     * Attempts to update the user information specified by the update form.
     * There can be empty fields. These empty fields are not updated.
     * If there are incorrect inputs (invalid email or name too short)
     * the errors are presented and no update attempt is made.
     */
    private void attemptUpdate() {
        if (mUserUpdateTask != null) {
            return;
        }

        //Reset errors.
        mPasswordView.setError(null);
        mEmailView.setError(null);

        //Store values at the time of the update attempt.
        mName = mNameView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mEmail = mEmailView.getText().toString();
        mStreet = mStreetView.getText().toString();
        mPlace = mPlaceView.getText().toString();
        mCountry = mCountryView.getText().toString();
        mZipcode = mZipcodeView.getText().toString();
        mBirthday = mBirthdayView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check fot a valid* email addresss.
        if (!TextUtils.isEmpty(mEmail) && !isEmailValid(mEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt update and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user update attempt.
            showProgress(true);
            mUserUpdateTask = new UserUpdateTask();
            mUserUpdateTask.execute((Void) null);
        }

    }

    /**
     * Attempts to delete the user specified by the username
     */
    private void attemptDelete() {
        if (mDeleteUserTask != null) {
            return;
        }

        mDeleteView.setError(null);
        String username = mDeleteView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mDeleteView.setError(getString(R.string.error_field_required));
            focusView = mDeleteView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mDeleteView.setError(getString(R.string.error_invalid_username));
            focusView = mDeleteView;
            cancel = true;
        }

        if (cancel)
            focusView.requestFocus();
        else {
            confirmDialog(username);
        }

    }

//    public void showDatePickerDialog(View v) {
//        DialogFragment dialogFragment = new DatePickerFragment();
//        dialogFragment.show(getSupportFragmentManager(), "datePicker");
//    }
//
//    public static class DatePickerFragment extends DialogFragment
//            implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//
//            // Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(getActivity(), this, year, month, day);
//        }
//
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            // Do something with the date chosen by the user
//
//        }
//    }


    private void confirmDialog(String username) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.confirm_delete);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            showProgress(true);
            mDeleteUserTask = new DeleteUserTask(username);
            mDeleteUserTask.execute((Void) null);
        });

        alertDialog.setNegativeButton("No", (dialog, which) -> {
            //DO nothing
        });

        alertDialog.show();
    }

    private boolean isUsernameValid(String username) {
        String mUsername = settings.getString("username", null);
        Log.i("username", mUsername);
        return username.equals(mUsername);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(this, UserProfileActivity.class);
        startActivity(back);
        super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mUpdateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mUpdateFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mUpdateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class UserUpdateTask extends AsyncTask<Void, Void, String> {

        UserUpdateTask() { }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                JSONObject credentials = new JSONObject();
                if (mName.isEmpty()) { mName = settings.getString("name", null); }
                credentials.accumulate("name", mName);
                Log.i("Name", mName);
                if (mPassword.isEmpty()) { mPassword = settings.getString("password", null); }
                credentials.accumulate("password", mPassword);
                Log.i("Password", mPassword);
                if (mEmail.isEmpty()) { mEmail = settings.getString("email", null); }
                credentials.accumulate("email", mEmail);
                Log.i("Email", mEmail);
                if (mStreet.isEmpty()) { mStreet = settings.getString("street", null); }
                credentials.accumulate("street", mStreet);
                if (mPlace.isEmpty()) { mPlace = settings.getString("place", null); }
                credentials.accumulate("place", mPlace);
                if (mCountry.isEmpty()) { mCountry = settings.getString("country", null); }
                credentials.accumulate("country", mCountry);
                if (mBirthday.isEmpty()) { mBirthday = settings.getString("birthday", null); }
                credentials.accumulate("birthday", mBirthday);
                if (mZipcode.isEmpty()) { mZipcode = settings.getString("zip_code", null); }
                credentials.accumulate("zipCode", mZipcode);
                String token = settings.getString("token", null);
                Log.e("Credentials", mName+" "+mPassword+" "+mEmail+" "+mStreet+" "+mPlace+" "+mCountry+" "+mZipcode+" "+mBirthday);
                Log.e("token", token);
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/update"), credentials, token);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mUserUpdateTask = null;
            showProgress(false);
            Log.i(TAG, "onPostExecute: before result: " +result);

            if (result != null) {
                Log.i(TAG, "onPostExecute: after result");
                SharedPreferences.Editor editor = settings.edit();
//                editor.putString("password", mPassword);
//                editor.putString("email", mPassword);

                //TODO: if's desnecessarios
                if (!mName.isEmpty())
                    editor.putString("name", mName);
                if (!mPassword.isEmpty())
                    editor.putString("password", mPassword);
                if (!mEmail.isEmpty())
                    editor.putString("email", mEmail);
                if (!mStreet.isEmpty())
                    editor.putString("street", mStreet);
                if (!mPlace.isEmpty())
                    editor.putString("place", mPlace);
                if (!mCountry.isEmpty())
                    editor.putString("country", mCountry);
                if (!mZipcode.isEmpty())
                    editor.putString("zip_code", mZipcode);
                if (!mBirthday.isEmpty())
                    editor.putString("birthday", mBirthday);
                editor.apply();

                Toast.makeText(EditUserProfileActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditUserProfileActivity.this, UserProfileActivity.class));
                finish();
            } else {
                Toast.makeText(EditUserProfileActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
            }
        }

        protected  void onCancelled() {
            mUserUpdateTask = null;
            showProgress(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class DeleteUserTask extends AsyncTask<Void, Void, String> {

        private  final String mUsername;

        DeleteUserTask(String username) {
            mUsername = username;
        }

        @Override
        protected String doInBackground(Void... voids) {
            JSONObject credential = new JSONObject();
            try{
                Log.i(TAG, "inside try-catch");
                credential.accumulate("username", mUsername);
//                String token = settings.getString("token", null).replaceAll("\"", "");
                String token = settings.getString("token", null);
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/user/makeAccountInactive"), null, token);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            mDeleteUserTask = null;
            showProgress(false);

            if(success != null){
                Session.setLogout(getApplicationContext());
                SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(EditUserProfileActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditUserProfileActivity.this, LoginActivity.class));
                finish();
            } else {
                mDeleteView.setError(getString(R.string.failed_delete));
                mDeleteView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mDeleteUserTask = null;
            showProgress(false);
        }

    }
}
