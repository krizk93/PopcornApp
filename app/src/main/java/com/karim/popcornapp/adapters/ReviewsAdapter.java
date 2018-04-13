package com.karim.popcornapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.karim.popcornapp.R;
import com.karim.popcornapp.data.Reviews;

import java.util.List;

/**
 * Created by Karim on 05-Apr-18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<Reviews> mDataset;
    private Context mContext;

    public ReviewsAdapter(Context context, List<Reviews> dataset) {
        mContext = context;
        mDataset = dataset;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        String author = mDataset.get(position).getAuthor();
        String content = mDataset.get(position).getContent();
        String text = mContext.getString(R.string.review_label, position + 1, mDataset.size());
        holder.mLabel.setText(text);
        holder.mAuthor.setText(author);
        holder.mContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class ReviewsViewHolder extends RecyclerView.ViewHolder {

        TextView mAuthor;
        TextView mContent;
        TextView mLabel;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            mAuthor = itemView.findViewById(R.id.review_author);
            mContent = itemView.findViewById(R.id.review_content);
            mLabel = itemView.findViewById(R.id.review_label);
        }
    }
}
