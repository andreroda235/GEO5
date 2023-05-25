package pt.unl.fct.di.www.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";

    private RecyclerView mCommeRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentRecyclerAdapter commentRecyclerAdapter;

    private Button replyBtn;
    private EditText mCommentEdit;
    private RelativeLayout mReplyLayout;

    private String geoSpotName;
    private List<CommentData> pulledGeoSpotComments;
    private boolean replyEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Intent intent = getIntent();
        geoSpotName =  intent.getStringExtra("geoSpotName");

        mCommeRecyclerView = findViewById(R.id.comment_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);

        mCommeRecyclerView.setLayoutManager(mLayoutManager);

        replyBtn = findViewById(R.id.comment_reply_btn);
        mCommentEdit = findViewById(R.id.comment_input);
        mReplyLayout = findViewById(R.id.comment_reply_layout);
        replyEnabled = true;

        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(replyEnabled) {
                    String comment = mCommentEdit.getText().toString();
                    if (comment.length() > 0) {
                        replyEnabled = false;
                        insertComment(comment);
                    }
                }else{
                    Toast.makeText(CommentsActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
                }
            }
        });

        PullGeoSpotCommentsTask pullGeoSpotCommentsTask = new PullGeoSpotCommentsTask(geoSpotName);
        pullGeoSpotCommentsTask.execute((Void) null);


    }

    public void insertComment(String comment){
        SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
        String username = settings.getString("username", null);
        String commentID = username + UUID.randomUUID().toString();
        CommentData newComment = new CommentData(geoSpotName, comment, username, commentID);

        //send comment to server
        PostGeoSpotCommentTask postGeoSpotCommentTask = new PostGeoSpotCommentTask(newComment);
        postGeoSpotCommentTask.execute((Void) null);

        pulledGeoSpotComments.add(newComment);
        commentRecyclerAdapter.notifyDataSetChanged();
        replyEnabled = true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class PostGeoSpotCommentTask extends AsyncTask<Void, Void, String> {

        Gson gson;
        private CommentData comment;


        //use boolean to separate update and load requests
        PostGeoSpotCommentTask(CommentData comment){
            gson = new Gson();
            this.comment = comment;

        }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
            String token = authentication.getString("token",null);

            JSONObject jsonComment = new JSONObject();
            try {
                jsonComment.accumulate("geoSpotName", comment.getGeoSpotName());
                jsonComment.accumulate("content", comment.getContent());
                jsonComment.accumulate("username", comment.getUsername());
                jsonComment.accumulate("commentID", comment.getCommentID());

                Log.d(TAG, "postComment-doInBackground: " + jsonComment.toString());

                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/comment/geoSpot"),jsonComment, token);
            } catch (Exception e) {
                Log.d(TAG, "PostGeoSpotComment-doInBackground: exception:" + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String success) {

            Log.d(TAG, "PostComment onPostExecute: result: " + success);
            if (success != null) {
                Toast.makeText(CommentsActivity.this, "Comment Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CommentsActivity.this, "Comment not saved.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class PullGeoSpotCommentsTask extends AsyncTask<Void, Void, String> {

        //use boolean to separate update and load requests
        private String geoSpot;

        PullGeoSpotCommentsTask(String geoSpot){
            this.geoSpot = geoSpot;
        }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
            String token = authentication.getString("token",null);

            geoSpot = geoSpot.replaceAll("\\s+", "%20");

            Log.d(TAG, "doInBackground: " + geoSpot);

            try {
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/comment/comment/geoSpot/"+ geoSpot),null, token);
            } catch (IOException e) {
                Log.d(TAG, "PullGeoSpotComments-doInBackground: exception:" + e.getMessage());
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String success) {

            if (success != null && !success.equals("[]")) {
                try {
                    Gson gson = new Gson();

                    Type geoSpotCommentListType = new TypeToken<ArrayList<CommentData>>(){}.getType();
                    pulledGeoSpotComments = gson.fromJson(success, geoSpotCommentListType);


                } catch (Exception e) {
                    Log.e("PullGeoSpotComments", e.toString());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CommentsActivity.this, "There are no comments at the moment.", Toast.LENGTH_SHORT).show();
                pulledGeoSpotComments = new ArrayList<>();
            }

            commentRecyclerAdapter = new CommentRecyclerAdapter(pulledGeoSpotComments);
            mCommeRecyclerView.setAdapter(commentRecyclerAdapter);
            mReplyLayout.setVisibility(View.VISIBLE);

        }
    }

}