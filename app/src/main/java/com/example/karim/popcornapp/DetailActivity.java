package com.example.karim.popcornapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.data.Movies;

import static com.example.karim.popcornapp.MovieAdapter.loadWithPicasso;

/**
 * Created by Karim on 17-Mar-18.
 */

public class DetailActivity extends AppCompatActivity {
    ImageView mimageView;
    TextView mAverageVote;
    TextView mTitle;
    TextView mOverview;
    TextView mReleaseDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        mimageView = findViewById(R.id.image_poster);
        mAverageVote = findViewById(R.id.tv_rating);
        mTitle = findViewById(R.id.tv_title);
        mOverview = findViewById(R.id.tv_overview);
        mReleaseDate = findViewById(R.id.tv_date);

        Intent intent = getIntent();
        Movies movie = intent.getParcelableExtra(getString(R.string.movie_details));

        Double voteAverage = movie.getVoteAverage();
        String posterPath = movie.getPosterPath();
        String originalTitle = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();
        getSupportActionBar().setTitle(originalTitle);
        loadWithPicasso(posterPath, getApplicationContext(), mimageView);
        mAverageVote.setText(String.valueOf(voteAverage) + "/10");
        mTitle.setText(originalTitle);
        mOverview.setText(overview);
        mReleaseDate.setText(releaseDate);

    }
}
