package pt.unl.fct.di.www.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.CommentViewHolder> {

    List<CommentData> comments;

    public CommentRecyclerAdapter (List<CommentData> comments){
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        RelativeLayout mCommentLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view,parent,false);
        CommentViewHolder commentViewHolder = new CommentViewHolder(mCommentLayout);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int i) {

        CommentData comment = comments.get(i);
        RelativeLayout commentLayout = holder.mCommentLayout;

        TextView mAuthor = commentLayout.findViewById(R.id.comment_author);
        mAuthor.setText(comment.getUsername() + " says:");

        //talvez devolver data também

        TextView mContent = commentLayout.findViewById(R.id.comment_content);
        mContent.setText(comment.getContent());

        commentLayout.setTag(comment.getCommentID());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout mCommentLayout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            mCommentLayout = itemView.findViewById(R.id.comment_layout);
        }
    }
}
