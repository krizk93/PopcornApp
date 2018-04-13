package com.karim.popcornapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.R;
import com.karim.popcornapp.data.Movies;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karim on 22-Mar-18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movies> mDataset;
    private Context mContext;
    private final PosterItemClickListener mOnClickListener;

    public interface PosterItemClickListener {
        void onItemClick(Movies movie);
    }

    public MovieAdapter(Context context, List<Movies> dataset, PosterItemClickListener listener) {
        mContext = context;
        mDataset = dataset;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String path = mDataset.get(position).getPosterPath();
        Uri uri = getPosterURI(path,mContext);
        Picasso.with(mContext).load(uri).into(holder.mImageView);
        holder.mPosterTitle.setText(mDataset.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;
        TextView mPosterTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.iv_poster);
            mPosterTitle = itemView.findViewById(R.id.tv_poster_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onItemClick(mDataset.get(clickedPosition));
        }
    }

    public static Uri getPosterURI(String path, Context context){
        return Uri.parse(context.getString(R.string.poster_url,path));
    }
}