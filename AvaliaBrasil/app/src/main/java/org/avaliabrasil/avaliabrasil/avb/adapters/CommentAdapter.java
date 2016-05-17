package org.avaliabrasil.avaliabrasil.avb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.javabeans.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 06/04/2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    /**
     *
     */
    private List<Comment> items = new ArrayList<>();

    /**
     *
     */
    private Context context;


    public CommentAdapter(Context context, List<Comment> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_comment_card_view, null);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comment comment = items.get(position);
        holder.comment.setText(comment.getDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView comment;

        public MyViewHolder(View view) {
            super(view);

            comment = (TextView) view.findViewById(R.id.tvComment);
        }
    }
}
