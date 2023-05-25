package pt.unl.fct.di.www.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;

public class  RegisterActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask;
    private RegisterUserTask mRegisterUserTask;

    private EditText mRegisterUsernameView;
    private EditText mRegisterFirstNameView;
    private EditText mRegisterLastNameView;
    private EditText mRegisterEmailView;
    private EditText mRegisterPasswordView;
    private EditText mRegisterConfirmPassView;

    private ActivateAccount mActivation = null;

    private boolean registered;

    private final String TAG = "RegisterActivity";

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
        String username = mRegisterUsernameView.getText().toString().trim();
        String firstName = mRegisterFirstNameView.getText().toString().trim();
        String lastName = mRegisterLastNameView.getText().toString().trim();
        String email = mRegisterEmailView.getText().toString().trim();
        String password = mRegisterPasswordView.getText().toString().trim();
        String confPassword = mRegisterConfirmPassView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if(mRegisterPasswordView.hasFocus())
            mRegisterPasswordView.setError(getString(R.string.password_requirement));

        // Check for empty and invalid fields
        if (TextUtils.isEmpty(username)) {
            mRegisterUsernameView.setError(getString(R.string.invalid_field));
            mRegisterUsernameView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(firstName)) {
            mRegisterFirstNameView.setError(getString(R.string.invalid_field));
            mRegisterFirstNameView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mRegisterLastNameView.setError(getString(R.string.invalid_field));
            mRegisterLastNameView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mRegisterEmailView.setError(getString(R.string.invalid_field));
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
            mRegisterConfirmPassView.setError(getString(R.string.password_mismatch));
            mRegisterConfirmPassView.requestFocus();
            cancel = true;
        }

        if( !cancel ) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);

            Log.d(TAG, "attemptRegister: " + firstName + " " + lastName);
            mRegisterUserTask = new RegisterUserTask(username,firstName + " " + lastName, email, password);
            mRegisterUserTask.execute((Void) null);

        }
    }

    private void attemptToActivate(String username) {
        if (mActivation != null) {
            return;
        }
        Log.d(TAG, "attemptToActivate: " + username);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the activation code");
        builder.setCancelable(false);
        final EditText codeInput = new EditText(this);
        final String[] result = new String[1];
        codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(codeInput);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result[0] = codeInput.getText().toString();
                mActivation = new ActivateAccount(username, result[0]);
                mActivation.execute((Void) null);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
            }
        });
        builder.show();
    }

    private boolean isPasswordValid(String password){
        return password.length() > 7;
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject credentials = new JSONObject();
                credentials.accumulate("username",mUsername);
                credentials.accumulate("password",mPassword);
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/login"),credentials, null);
            } catch (Exception e) {
                Log.e(TAG+" Login", e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            //showProgress(false);

            if (result != null) {
                Session.setLogin(getApplicationContext());
                SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", mUsername);
                editor.putString("password", mPassword);
                String token = result.replaceAll("\"", "");
                editor.putString("token", token);
                editor.apply();

                Toast.makeText(RegisterActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, UserAreaActivity.class));
                finish();
            } else {
                mRegisterUsernameView.setError(getString(R.string.error_invalid_username_password));
                mRegisterUsernameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
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
                //TODO
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/register/confirm"),registerData, null);
//                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/register"),registerData, null);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            //showProgress(false);
            mRegisterUserTask = null;

            if (success != null) {
                Log.d(TAG, "register doInBackground: " + mUsername + " " + mPassword);

                Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", mUsername);
                editor.putString("password", mPassword);
                editor.apply();
                attemptToActivate(mUsername);

            } else {
                Toast.makeText(RegisterActivity.this, R.string.invalid_register, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterUserTask = null;
            //showProgress(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ActivateAccount extends AsyncTask<Void, Void, String> {
        private final String mUsername;
        private final String mCode;

        public ActivateAccount(String username, String code) {
            mUsername = username;
            mCode = code;
        }

        @Override
        protected String doInBackground(Void... voids) {
            JSONObject activationCode = new JSONObject();
            try {
                Log.d(TAG, "Activation doInBackground: " + mUsername);
                activationCode.accumulate("username", mUsername);
                activationCode.accumulate("activationCode", mCode);
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/user/activateAccount"), activationCode, null);
            } catch (Exception e) {
                Log.e("Account activation", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            mAuthTask = null;
            if (s != null) {
                Toast.makeText(RegisterActivity.this, "Activation successful", Toast.LENGTH_SHORT).show();
                SharedPreferences cache = getSharedPreferences("AUTHENTICATION", 0);
                String password = cache.getString("password", null);
                mAuthTask = new UserLoginTask(mUsername, password);
                mAuthTask.execute((Void) null);
            }
            else
                Toast.makeText(RegisterActivity.this, "Activation failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            mActivation = null;
        }
    }

}
