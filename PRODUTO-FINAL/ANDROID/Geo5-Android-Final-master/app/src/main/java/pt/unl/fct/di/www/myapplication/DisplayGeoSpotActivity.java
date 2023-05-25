package pt.unl.fct.di.www.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisplayGeoSpotActivity extends AppCompatActivity {

    private static final String TAG = "DisplayGeoSpotActivity";
    private final int FIVE_MINS = /*5 * 60*/5 * 1000;

    private GeoSpotData data;
    private ImageView recipient;
    private Bitmap bitmap;
    private List<String> photoUrls;
    private int urlPosition;
    private Button quizAvailable;
    private Button quizUnavailable;
    private RelativeLayout photoButtons;
    private TextView loading;

    SharedPreferences cache;
    private Long lastQuizTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_geo_spot);

        Intent intent = getIntent();
        data = (GeoSpotData) intent.getSerializableExtra("geoSpotData");
        Log.d(TAG, "onCreate: " + data.getGeoSpotName() + " ," + data.getLocation() + " ," + data.getDescription() + " ," + data.getTags());

        cache = getSharedPreferences("AUTHENTICATION", 0);
        lastQuizTime = cache.getLong(data.getGeoSpotName() , -1);
        Log.d(TAG, "onCreate: " + lastQuizTime);

        recipient = findViewById(R.id.geospot_image_recipient);
        quizAvailable = findViewById(R.id.geospot_quizz);
        quizUnavailable = findViewById(R.id.geospot_quizz_unavailable);
        photoButtons = findViewById(R.id.geospot_photo_buttons_layout);
        loading = findViewById(R.id.geospot_loading_photo);
        photoUrls = new ArrayList<>();

        createData();

    }

    private void createData(){

        GetGeoSpotPhotosTask getGeoSpotPhotosTask = new GetGeoSpotPhotosTask(data.getGeoSpotName());
        getGeoSpotPhotosTask.execute((Void) null);

        TextView mGeoSpotName = findViewById(R.id.geospot_name);
        mGeoSpotName.setText(data.getGeoSpotName());

        TextView mGeoSpotDescription = findViewById(R.id.geospot_description);
        mGeoSpotDescription.setText(data.getDescription());
        mGeoSpotDescription.setMovementMethod(new ScrollingMovementMethod());

    }

    private String getTag(){
        String[] tags = data.getTags().trim().split(",");
        Random random = new Random();
        int i = random.nextInt(tags.length);
        return tags[i];
    }

    private void loadPhotos(ArrayList<String> photoNames) {

        if(photoNames.size() > 0) {
            for (String name : photoNames) {
                String url = "https://storage.googleapis.com/apdc-geoproj.appspot.com/" + name;
                photoUrls.add(url);
            }
            urlPosition = 0;
            updateRecipient(urlPosition);
        }

    }

    public void checkButton(View button){
        int id = button.getId();

        switch (id){
            case R.id.geospot_comments:{
                Intent comments = new Intent(DisplayGeoSpotActivity.this, CommentsActivity.class);
                comments.putExtra("geoSpotName", data.getGeoSpotName());
                startActivity(comments);
                break;
            }
            case R.id.geospot_quizz:{
                Intent quiz = new Intent(DisplayGeoSpotActivity.this, QuizzActivity.class);
                quiz.putExtra("quizTag", getTag());
                SharedPreferences.Editor editor = cache.edit();
                editor.putLong(data.getGeoSpotName(), System.currentTimeMillis());
                editor.apply();

                startActivity(quiz);
                finish();
                break;
            }
            case R.id.geospot_quizz_unavailable:{
                    Long interval = System.currentTimeMillis() - lastQuizTime;
                    if (interval >= FIVE_MINS) {
                        Toast.makeText(DisplayGeoSpotActivity.this, "Click again for Quiz!", Toast.LENGTH_SHORT).show();
                        quizUnavailable.setVisibility(View.GONE);
                        quizAvailable.setVisibility(View.VISIBLE);
                    }else{
                        Long remaining = FIVE_MINS - interval;
                        Long minutes = (remaining / 1000) / 60;
                        int seconds = (int) ((remaining / 1000) % 60 );
                        Toast.makeText(DisplayGeoSpotActivity.this, "Please wait " + minutes + "min" + " : " + seconds + "s" , Toast.LENGTH_SHORT).show();
                    }

                break;
            }
        }
    }

    public void navigatePhoto(View btn) {
        int id = btn.getId();

        switch (id){
            case R.id.geospot_photo_next:{
                updateRecipient(next());
                break;
            }
            case R.id.geospot_photo_previous:{
                updateRecipient(previous());
                break;
            }
        }
        photoButtons.setVisibility(View.GONE);
    }

    private void updateRecipient(int pos){
        Log.d(TAG, "updateRecipient: " + pos);
        String url = photoUrls.get(pos);
        DownloadFileTask downloadFileTask = new DownloadFileTask(url);
        downloadFileTask.execute((Void) null);
        /*bitmap = getResizedBitmap(getBitmapFromURL(url), 300, 200);
        recipient.setImageBitmap(bitmap);*/

    }

    private int next(){

        if(photoUrls.size() > 0){
            if(urlPosition == photoUrls.size()-1)
                urlPosition = urlPosition % photoUrls.size()-1;
            else
                urlPosition++;
        }else
            urlPosition = -1;

        return urlPosition;
    }

    private int previous(){

        if(photoUrls.size() > 0){
            if(urlPosition == 0){
                urlPosition = photoUrls.size()-1;
            }else
                urlPosition--;
        }else
            urlPosition = -1;

        return urlPosition;
    }

    @Override
    public void onBackPressed() {
        if(bitmap != null){
            bitmap.recycle();
        }
        super.onBackPressed();
    }

    public class DownloadFileTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;

        DownloadFileTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            return SampledPhotos.getBitmapFromURL(url);
        }

        @Override
        protected void onPostExecute(final Bitmap result) {


            if (result != null) {

                bitmap = SampledPhotos.getResizedBitmap(result, 250, 350);
                loading.setVisibility(View.GONE);
                recipient.setImageBitmap(bitmap);
                photoButtons.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(DisplayGeoSpotActivity.this, "couldn't get file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class GetGeoSpotPhotosTask extends AsyncTask<Void, Void, String> {
        private String geoSpotName;

        GetGeoSpotPhotosTask(String geoSpotName) {
            this.geoSpotName = geoSpotName;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
                String token = authentication.getString("token",null);

                geoSpotName = geoSpotName.replaceAll("\\s+", "%20");

                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/geoSpot/"+ geoSpotName + "/pictures"), null, token);
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: ");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {


            if (result != null && !result.equals("[]")) {

                Gson gson = new Gson();

                Type photoNameListType = new TypeToken<ArrayList<String>>(){}.getType();
                ArrayList<String> photoNames = gson.fromJson(result, photoNameListType);

                loadPhotos(photoNames);

            } else {
                Toast.makeText(DisplayGeoSpotActivity.this, "This path has no photos.", Toast.LENGTH_SHORT).show();
                loading.setText("No Photos");
            }
        }
    }
}