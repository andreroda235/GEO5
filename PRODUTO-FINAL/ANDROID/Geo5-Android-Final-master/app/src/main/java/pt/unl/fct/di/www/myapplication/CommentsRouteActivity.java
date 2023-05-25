package pt.unl.fct.di.www.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentsRouteActivity extends AppCompatActivity {

    private static final String TAG = "CommentsRouteActivity";
    private RecyclerView mCommeRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentRouteRecyclerAdapter commentRecyclerAdapter;

    private Button replyBtn;
    private EditText mCommentEdit;
    private RelativeLayout mReplyLayout;

    private String routeID;
    private List<CommentRouteData> pulledRouteComments;
    private boolean replyEnabled;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_route);

        Intent intent = getIntent();
        routeID =  intent.getStringExtra("routeID");

        mCommeRecyclerView = findViewById(R.id.comment_route_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);

        mCommeRecyclerView.setLayoutManager(mLayoutManager);

        replyBtn = findViewById(R.id.comment_route_reply_btn);
        mCommentEdit = findViewById(R.id.comment_route_input);
        mReplyLayout = findViewById(R.id.comment_route_reply_layout);
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
                    Toast.makeText(CommentsRouteActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
                }
            }
        });

        PullRouteCommentsTask pullRouteCommentsTask = new PullRouteCommentsTask(routeID);
        pullRouteCommentsTask.execute((Void) null);

    }

    public void insertComment(String comment){
        SharedPreferences settings = getSharedPreferences("AUTHENTICATION", 0);
        String username = settings.getString("username", null);
        String commentID = username + UUID.randomUUID().toString();
        CommentRouteData newComment = new CommentRouteData(routeID, comment, username, commentID);

        //send comment to server
        PostRouteCommentTask postGeoSpotCommentTask = new PostRouteCommentTask(newComment);
        postGeoSpotCommentTask.execute((Void) null);

        pulledRouteComments.add(newComment);
        commentRecyclerAdapter.notifyDataSetChanged();
        replyEnabled = true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class PostRouteCommentTask extends AsyncTask<Void, Void, String> {

        Gson gson;
        private CommentRouteData comment;


        //use boolean to separate update and load requests
        PostRouteCommentTask(CommentRouteData comment){
            gson = new Gson();
            this.comment = comment;

        }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
            String token = authentication.getString("token",null);

            JSONObject jsonComment = new JSONObject();
            try {
                jsonComment.accumulate("routeID", comment.getRouteID());
                jsonComment.accumulate("content", comment.getContent());
                jsonComment.accumulate("username", comment.getUsername());
                jsonComment.accumulate("commentID", comment.getCommentID());

                Log.d(TAG, "postComment-doInBackground: " + jsonComment.toString());

                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/comment/route"),jsonComment, token);
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
                Toast.makeText(CommentsRouteActivity.this, "Comment Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CommentsRouteActivity.this, "Comment not saved.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class PullRouteCommentsTask extends AsyncTask<Void, Void, String> {

        //use boolean to separate update and load requests
        private String route;

        PullRouteCommentsTask(String route){
            this.route = route;
        }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
            String token = authentication.getString("token",null);

            Log.d(TAG, "doInBackground: " + route);

            try {
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/comment/comment/route/" + route),null, token);
            } catch (IOException e) {
                Log.d(TAG, "PullRouteComments-doInBackground: exception:" + e.getMessage());
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String success) {

            if (success != null && !success.equals("[]")) {
                try {
                    Gson gson = new Gson();

                    Type geoSpotCommentListType = new TypeToken<ArrayList<CommentRouteData>>(){}.getType();
                    pulledRouteComments = gson.fromJson(success, geoSpotCommentListType);


                } catch (Exception e) {
                    Log.e("PullGeoSpotComments", e.toString());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CommentsRouteActivity.this, "There are no comments at the moment.", Toast.LENGTH_SHORT).show();
                pulledRouteComments = new ArrayList<>();
            }

            commentRecyclerAdapter = new CommentRouteRecyclerAdapter(pulledRouteComments);
            mCommeRecyclerView.setAdapter(commentRecyclerAdapter);
            mReplyLayout.setVisibility(View.VISIBLE);

        }
    }


}
