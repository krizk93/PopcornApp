package com.karim.popcornapp.adapters;

import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.R;
import com.karim.popcornapp.data.Movies;
import com.karim.popcornapp.persistence.FavoritesContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karim on 22-Mar-18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movies> mDataset;
    private Context mContext;
    private Cursor mCursor;
    private final PosterItemClickListener mOnClickListener;

    public interface PosterItemClickListener {
        void onItemClick(Movies movie);
    }

    public MovieAdapter(Context context, List<Movies> dataset, PosterItemClickListener listener) {
        mContext = context;
        mDataset = dataset;
        mOnClickListener = listener;
    }

    public MovieAdapter(Context context, PosterItemClickListener listener) {
        mContext = context;
        mDataset = null;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (mDataset != null) {
            String path = mDataset.get(position).getPosterPath();
            Uri uri = getPosterURI(path, mContext);
            Picasso.with(mContext).load(uri).into(holder.mImageView);
            holder.mPosterTitle.setText(mDataset.get(position).getTitle());
        } else if (mCursor != null) {
            if (!mCursor.moveToPosition(position))
                return;
            String path = mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POSTER_ID
            ));
            String title = mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_TITLE));
            Uri uri = getPosterURI(path, mContext);
            Picasso.with(mContext).load(uri).into(holder.mImageView);
            holder.mPosterTitle.setText(title);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        } else if (mCursor != null) {
            return mCursor.getCount();
        } else return 0;
    }

    public static Uri getPosterURI(String path, Context context) {
        return Uri.parse(context.getString(R.string.poster_url, path));
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        mDataset = null;
        notifyDataSetChanged();
    }

    public Movies getDataFromCursor(Cursor cursor) {
        Movies movie = new Movies();
        movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID))));
        movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POSTER_ID)));
        movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_TITLE)));
        movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE)));
        movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RATING))));
        movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE)));
        movie.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_LANGUAGE)));
        movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW)));
        return movie;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;
        TextView mPosterTitle;

        MovieViewHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.iv_poster);
            mPosterTitle = itemView.findViewById(R.id.tv_poster_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            if (mDataset == null) {
                Movies movie;
                mCursor.moveToPosition(clickedPosition);
                movie = getDataFromCursor(mCursor);
                mOnClickListener.onItemClick(movie);
            } else
                mOnClickListener.onItemClick(mDataset.get(clickedPosition));

        }
    }
}
