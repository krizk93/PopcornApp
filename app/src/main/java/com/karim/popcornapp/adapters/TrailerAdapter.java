package com.karim.popcornapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.R;
import com.karim.popcornapp.data.Videos;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karim on 03-Apr-18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.VideoViewHolder> {

    private List<Videos> mDataset;
    private Context mContext;
    private final TrailerItemClickListener mOnClickListener;

    public interface TrailerItemClickListener {
        void onItemClick(Videos video);
    }

    public TrailerAdapter(Context context, List<Videos> dataset, TrailerItemClickListener listener) {
        mContext = context;
        mDataset = dataset;
        mOnClickListener = listener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String name = mDataset.get(position).getName();
        String path = mDataset.get(position).getKey();
        String videoUrl = mContext.getString(R.string.thumbnail_url, path);
        Picasso.with(mContext).load(videoUrl).into(holder.mVideo);
        holder.mTitle.setText(name);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mVideo;
        TextView mTitle;

        public VideoViewHolder(View itemView) {
            super(itemView);

            mVideo = itemView.findViewById(R.id.trailer_video);
            mTitle = itemView.findViewById(R.id.trailer_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onItemClick(mDataset.get(clickedPosition));
        }
    }


}
