package pt.unl.fct.di.www.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PathManagerActivity extends AppCompatActivity {

    private static final String TAG = "PathManagerActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_manager);

        if(isServicesOK()){

        }

    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PathManagerActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PathManagerActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is fine
            Log.d(TAG, "isServicesVersionOK: google play services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //resolvable error
            Log.d(TAG, "isServicesVersionOK: a fixable error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PathManagerActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{
            Toast.makeText(this, "unable to make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
