package pt.unl.fct.di.www.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask;
    private RegisterUserTask mRegisterUserTask;

    private EditText mRegisterUsernameView;
    private EditText mRegisterFirstNameView;
    private EditText mRegisterLastNameView;
    private EditText mRegisterEmailView;
    private EditText mRegisterPasswordView;
    private EditText mRegisterConfirmPassView;

    private boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterUsernameView = (EditText) findViewById(R.id.register_username);
        mRegisterFirstNameView = (EditText) findViewById(R.id.register_first_name);
        mRegisterLastNameView = (EditText) findViewById(R.id.register_last_name);
        mRegisterEmailView = (EditText) findViewById(R.id.register_email);
        mRegisterPasswordView = (EditText) findViewById(R.id.register_password);
        mRegisterConfirmPassView = (EditText) findViewById(R.id.register_confirm_password);

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        if (mRegisterUserTask != null && mAuthTask != null) {
            return;
        }

        // Reset errors.
        mRegisterUsernameView.setError(null);
        mRegisterFirstNameView.setError(null);
        mRegisterLastNameView.setError(null);
        mRegisterEmailView.setError(null);
        mRegisterPasswordView.setError(null);
        mRegisterConfirmPassView.setError(null);

        // Store values at the time of the login attempt.
        String username = mRegisterUsernameView.getText().toString();
        String firstName = mRegisterFirstNameView.getText().toString();
        String lastName = mRegisterLastNameView.getText().toString();
        String email = mRegisterEmailView.getText().toString();
        String password = mRegisterPasswordView.getText().toString();
        String confPassword = mRegisterPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        if (TextUtils.isEmpty(username)) {
            mRegisterUsernameView.setError("Invalid Field!");
            mRegisterUsernameView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(firstName)) {
            mRegisterFirstNameView.setError("Invalid Field!");
            mRegisterFirstNameView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mRegisterLastNameView.setError("Invalid Field!");
            mRegisterLastNameView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mRegisterEmailView.setError("Invalid Field!");
            mRegisterEmailView.requestFocus();
            cancel = true;
        } else if (!isEmailValid(email)) {
            mRegisterEmailView.setError(getString(R.string.error_invalid_email));
            mRegisterEmailView.requestFocus();
            cancel = true;
    }

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mRegisterPasswordView.setError(getString(R.string.error_invalid_password));
            mRegisterPasswordView.requestFocus();
            cancel = true;
        }

        if (!confPassword.equals(password)) {
            mRegisterConfirmPassView.setError("Password Mismatch!");
            mRegisterConfirmPassView.requestFocus();
            cancel = true;
        }

        if( !cancel ) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mRegisterUserTask = new RegisterUserTask(username,firstName + " " + lastName, email, password);
            mRegisterUserTask.execute((Void) null);

        }
    }

    private boolean isPasswordValid(String password){
        return password.length() > 7;
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                JSONObject credentials = new JSONObject();
                credentials.accumulate("username",mUsername);
                credentials.accumulate("password",mPassword);
                //               return RequestsREST.doPOST(new URL("https://firstwebapplication-233617.appspot.com/rest/login"),credentials);
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/login"),credentials, null);
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            //showProgress(false);

            if (result != null) {

                SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
                SharedPreferences.Editor editor = settings.edit();
                //TODO: encrypt data
                editor.putString("username", mUsername);
                editor.putString("token", result);
                editor.commit();

                Log.i("LoginActivity", "HERE");

                Toast.makeText(RegisterActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, UserAreaActivity.class));

            } else {
                mRegisterUsernameView.setError("Username already in use.");
                mRegisterUsernameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

    public class RegisterUserTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mName;
        private final String mEmail;
        private final String mPassword;

        RegisterUserTask(String username, String name, String email, String password) {
            mUsername = username;
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                JSONObject registerData = new JSONObject();
                registerData.accumulate("username",mUsername);
                registerData.accumulate("name",mName);
                registerData.accumulate("email",mEmail);
                registerData.accumulate("password",mPassword);

                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/register"),registerData, null);
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            //showProgress(false);

            if (success != null) {
                mAuthTask = new UserLoginTask(mUsername, mPassword);
                mAuthTask.execute((Void) null);
            } else {
                mRegisterUsernameView.setError("Username already in use.");
                mRegisterUsernameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

}
