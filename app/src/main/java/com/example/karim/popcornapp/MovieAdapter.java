package com.example.karim.popcornapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.data.Movies;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karim on 22-Mar-18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    public static final String BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String size = "w500";

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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String path = mDataset.get(position).getPosterPath();
        loadWithPicasso(path, mContext, holder.mImageView);
        holder.mPosterTitle.setText(mDataset.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;
        TextView mPosterTitle;

        public MyViewHolder(View itemView) {
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

    public static void loadWithPicasso(String path, Context context, ImageView imageView) {
        path = path.replaceAll("/", "");
        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(size)
                .appendPath(path)
                .build();
        Picasso.with(context).load(uri).into(imageView);
    }
}
