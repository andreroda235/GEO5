package pt.unl.fct.di.www.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private DeleteUserTask mDeleteUserTask = null;
    private EditText mUsernameView;
    private View mProgressView;
    private View mDeleteFormView;
    private SharedPreferences settings;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getAppLocale();

        mUsernameView = findViewById(R.id.user_2_delete);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));

        Button changeLang = findViewById(R.id.changeLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show alert dialog to display list of languages, only one can be selected
                showChangeLangDialog();
            }
        });

        Button deleteAccButton = findViewById(R.id.delete_acc_button);
        deleteAccButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mUsernameView.setVisibility(View.VISIBLE);
                attemptDelete();
            }
        });

        mProgressView = findViewById(R.id.delete_progress);
        mDeleteFormView = findViewById(R.id.delete_form);

        settings = getSharedPreferences("AUTHENTICATION", 0);

    }

    private void attemptDelete() {
        if (mDeleteUserTask != null) {
            return;
        }

        mUsernameView.setError(null);
        String email = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel)
            focusView.requestFocus();
        else {
            showProgress(true);
            mDeleteUserTask = new DeleteUserTask(email);
            mDeleteUserTask.execute((Void) null);
        }

    }

    private boolean isUsernameValid(String username) {
        String mUsername = settings.getString("username", null);
        Log.i("Email do preferences:", mUsername);
        return username.equals(mUsername);
    }

    private void showChangeLangDialog() {
        final String[] items = {"English", "Portuguese"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(R.string.languages);
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //English
                        setAppLocale("EN");
//                        recreate();
                    case 1:
                        //Portuguese
                        setAppLocale("PT");
//                        recreate();
                }
                dialog.dismiss();
                finish();
                startActivity(getIntent());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
        //TODO
//        public void onRadioButtonClicked (View view){
//            //Check if the button is selected
//            boolean selected = ((RadioButton) view).isChecked();
//
//            //Get the text from the RadioButton view
//            RadioButton rb = (RadioButton) view;
//            String radioButtonText = rb.getText().toString();
//
////            //Check which radio button was clicked
//            switch (view.getId()) {
//                case R.id.en_lang:
//                case R.id.pt_lang:
//                    if (selected) setAppLocale(radioButtonText);
//                    break;
//            }
//        }
    }

        private void showProgress(final boolean show) {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mDeleteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mDeleteFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDeleteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mDeleteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                    Log.i("I AM HERE", "inside try-catch");
                    credential.accumulate("username", mUsername);
                    String token = settings.getString("token", null).replaceAll("\"", "");
                    return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/delete"), credential, token);
                } catch (Exception e) {
                    Log.e("Catch clause", e.toString());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final String success) {
                mDeleteUserTask = null;
                showProgress(false);

                if(success != null){
                    Toast.makeText(SettingsActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    finish();
                } else {
                    mUsernameView.setError(getString(R.string.failed_delete));
                    mUsernameView.requestFocus();
                }
            }

            @Override
            protected void onCancelled() {
                mDeleteUserTask = null;
                showProgress(false);
            }

        }
    }
