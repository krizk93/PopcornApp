package com.example.karim.popcornapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.data.Videos;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karim on 03-Apr-18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.VideoViewHolder> {

    private List<Videos> mDataset;
    private Context mContext;

    public TrailerAdapter(Context context, List<Videos> dataset){
        mContext = context;
        mDataset = dataset;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item,parent,false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
       String name = mDataset.get(position).getName();
       String path = mDataset.get(position).getKey();
       String videoUrl = "https://img.youtube.com/vi/" + path + "/hqdefault.jpg";
        Picasso.with(mContext).load(videoUrl).into(holder.mVideo);
       //Bitmap bMap = ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MINI_KIND);
       //holder.mVideo.setImageBitmap(bMap);
       holder.mTitle.setText(name);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        ImageView mVideo;
        TextView mTitle;

        public VideoViewHolder(View itemView) {
            super(itemView);

            mVideo = itemView.findViewById(R.id.trailer_video);
            mTitle = itemView.findViewById(R.id.trailer_title);
        }
}


}
