package com.example.karim.popcornapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.data.Movies;

import static com.example.karim.popcornapp.MovieAdapter.loadWithPicasso;

/**
 * Created by Karim on 17-Mar-18.
 */

public class DetailActivity extends AppCompatActivity {
    ImageView mImageView;
    TextView mAverageVote;
    TextView mTitle;
    TextView mOverview;
    TextView mReleaseDate;
    View mBackgroundView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        mImageView = findViewById(R.id.image_poster);
        mAverageVote = findViewById(R.id.tv_rating);
        mTitle = findViewById(R.id.tv_title);
        mOverview = findViewById(R.id.tv_overview);
        mReleaseDate = findViewById(R.id.tv_date);
        mBackgroundView = findViewById(R.id.background_view);

        Intent intent = getIntent();
        Movies movie = intent.getParcelableExtra(getString(R.string.movie_details));

        //retrieving data from parcel
        Integer id = movie.getId();
        Double voteAverage = movie.getVoteAverage();
        String posterPath = movie.getPosterPath();
        String originalTitle = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();

        //actionbar
        getSupportActionBar().setTitle(originalTitle);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.actionbar_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //loading content
        loadWithPicasso(posterPath, getApplicationContext(), mImageView);
        Drawable background = mImageView.getDrawable().getConstantState().newDrawable();
        mBackgroundView.setBackground(background);
        mBackgroundView.getBackground().mutate().setAlpha(35);
        mAverageVote.setText(String.valueOf(voteAverage) + "/10");
        mTitle.setText(originalTitle);
        mOverview.setText(overview);
        mReleaseDate.setText(releaseDate);

    }
}
