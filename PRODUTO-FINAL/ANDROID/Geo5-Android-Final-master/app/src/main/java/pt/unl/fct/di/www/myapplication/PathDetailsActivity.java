package pt.unl.fct.di.www.myapplication;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PathDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PathDetailsActivity";
    private static final int GALLERY_REQUEST = 9;
    private static final int CAMERA_REQUEST = 11;

    private boolean zoomOut;

    private ImageView recipient;
    private Button commentBtn;
    private Button picBtn;
    private TextView loading;
    private RelativeLayout picButtons;

    private Bitmap bitmap;
    private List<String> photoUrls;

    private int urlPosition;
    private String routeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_details);
        recipient = findViewById(R.id.route_photo_recipient);

        photoUrls = new ArrayList<>();
        zoomOut = false;
        urlPosition = 0;
        Intent intent = getIntent();
        routeID = intent.getStringExtra("routeID");
        String routeOwner = intent.getStringExtra("owner");
        SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
        String user = settings.getString("username", null);
        commentBtn = findViewById(R.id.route_comments_btn);
        picBtn = findViewById(R.id.add_photo_btn);
        loading = findViewById(R.id.route_loading_photo);
        picButtons = findViewById(R.id.route_photo_buttons_layout);
        if(!routeOwner.equals(user)){
            commentBtn.setVisibility(View.VISIBLE);
        }else{
            picBtn.setVisibility(View.VISIBLE);
        }

        createContent();

    }

    private void createContent() {

        getRoutePhotosTask getPhotos = new getRoutePhotosTask(routeID);
        getPhotos.execute((Void) null);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        TextView mTitle = findViewById(R.id.details_title);
        mTitle.setText(title);
        mTitle.setMovementMethod(new ScrollingMovementMethod());

        TextView mDescription = findViewById(R.id.route_description);
        mDescription.setText(description);
        mDescription.setMovementMethod(new ScrollingMovementMethod());

        Button addPhoto = findViewById(R.id.add_photo_btn);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageOptionDialog();
            }
        });

    }

    private void showImageOptionDialog(){
        final String[] options = new String[1];
        options[0] = "Pick from Gallery";
        AlertDialog.Builder builder = new AlertDialog.Builder(PathDetailsActivity.this);
        builder.setTitle("Select Source")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                                getImageFromGallery();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void navigatePhoto(View btn) {
        int id = btn.getId();

        switch (id){
            case R.id.route_photo_next:{
                updateRecipient(next());
                break;
            }
            case R.id.route_photo_previous:{
                updateRecipient(previous());
                break;
            }
        }
    }

    private void updateRecipient(int pos){
        String url = photoUrls.get(pos);
        picButtons.setVisibility(View.GONE);
        DownloadFileTask downloadFileTask = new DownloadFileTask(url);
        downloadFileTask.execute((Void) null);
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


    //Open phone gallery
    private void getImageFromGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check if the intent was to pick image, was successful and an image was picked
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            //Get selected image uri from phone gallery
            try {
            Uri selectedImage = data.getData();
            String mimeType = getMimeType(selectedImage);
            InputStream in = getContentResolver().openInputStream(selectedImage);
            byte[] inputData = getBytes(in);

            bitmap = SampledPhotos.getResizedBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage), 250,350);
            recipient.setImageBitmap(bitmap);
            SaveRoutePhotoTask savePhotoTask = new SaveRoutePhotoTask(mimeType,inputData,routeID);
            savePhotoTask.execute((Void) null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private byte[] getBytes(InputStream input) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = input.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getMimeType(Uri uri){
        ContentResolver cR = PathDetailsActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uri));
        return type;
    }


    public void checkButton(View v){
        Intent comments = new Intent(PathDetailsActivity.this, CommentsRouteActivity.class);
        comments.putExtra("routeID", routeID);
        startActivity(comments);
    }

    private void loadPhotos(ArrayList<String> photoNames) {

        if(photoNames.size() > 0) {
            for (String name : photoNames) {
                String url = "https://storage.googleapis.com/apdc-geoproj.appspot.com/" + name;
                photoUrls.add(url);
            }
            urlPosition = 0;
            DownloadFileTask downloadFileTask = new DownloadFileTask(photoUrls.get(urlPosition));
            downloadFileTask.execute((Void) null);
        }

    }

    public void exit(View view) {
        if(bitmap != null)
            bitmap.recycle();
        finish();
    }

    @Override
    public void onBackPressed() {
        if(bitmap != null)
            bitmap.recycle();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public class getRoutePhotosTask extends AsyncTask<Void, Void, String> {
        private final String routeID;

        getRoutePhotosTask(String routeID) {
            this.routeID = routeID;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
                String token = authentication.getString("token",null);

                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/route/" + routeID + "/pictures"), null, token);
            } catch (IOException e) {
                Toast.makeText(PathDetailsActivity.this, "Couldn't download path photos.", Toast.LENGTH_SHORT).show();

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
                loading.setVisibility(View.GONE);
                loadPhotos(photoNames);

            } else {
                Toast.makeText(PathDetailsActivity.this, "This path has no photos.", Toast.LENGTH_SHORT).show();
                loading.setText("No Photos");
            }
        }
    }

    public class SaveRoutePhotoTask extends AsyncTask<Void, Void, String> {
        private String mimeType;
        private byte[] data;
        private String routeID;

        SaveRoutePhotoTask(String mimeType, byte[] data, String routeID) {
            this.mimeType = mimeType;
            this.data = data;
            this.routeID = routeID;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
                String token = authentication.getString("token",null);

                return RequestsREST.doPOSTPic(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/storage/upload/route/" + routeID), mimeType, data, token);
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {


            if (result != null) {
                Toast.makeText(PathDetailsActivity.this, "photo saved!.", Toast.LENGTH_SHORT).show();
                getRoutePhotosTask getPhotos = new getRoutePhotosTask(routeID);
                getPhotos.execute((Void) null);
            } else {
                Toast.makeText(PathDetailsActivity.this, "Photo not saved", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }

            picBtn.setVisibility(View.VISIBLE);
        }
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
                loading.setVisibility(View.GONE);
                picButtons.setVisibility(View.VISIBLE);
                bitmap = SampledPhotos.getResizedBitmap(result, 250, 350);
                recipient.setImageBitmap(bitmap);
            } else {
                Toast.makeText(PathDetailsActivity.this, "couldn't get file", Toast.LENGTH_SHORT).show();
            }
        }
    }

}