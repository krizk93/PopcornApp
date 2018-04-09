package com.example.karim.popcornapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karim.popcornapp.api.Client;
import com.example.karim.popcornapp.api.Service;
import com.example.karim.popcornapp.data.Movies;
import com.example.karim.popcornapp.data.Reviews;
import com.example.karim.popcornapp.data.ReviewsResults;
import com.example.karim.popcornapp.data.VideoResults;
import com.example.karim.popcornapp.data.Videos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.karim.popcornapp.MovieAdapter.loadWithPicasso;

/**
 * Created by Karim on 17-Mar-18.
 */

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerItemClickListener {
    private ImageView mImageView;
    private TextView mAverageVote;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private View mBackgroundView;
    private Context mContext;
    private RecyclerView mTrailersRecyclerView;
    private RecyclerView.Adapter mTrailersAdapter;
    private RecyclerView mReviewsRecyclerView;
    private RecyclerView.Adapter mReviewsAdapter;
    private TextView mNoTrailersTV;
    private TextView mNoReviewsTV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        mContext = getApplicationContext();
        mImageView = findViewById(R.id.image_poster);
        mAverageVote = findViewById(R.id.tv_rating);
        mTitle = findViewById(R.id.tv_title);
        mOverview = findViewById(R.id.tv_overview);
        mReleaseDate = findViewById(R.id.tv_date);
        mBackgroundView = findViewById(R.id.background_view);
        mNoTrailersTV = findViewById(R.id.no_trailers);
        mNoReviewsTV = findViewById(R.id.no_reviews);

        mTrailersRecyclerView = findViewById(R.id.trailers_recycler_view);
        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this);
        trailersLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTrailersRecyclerView.setLayoutManager(trailersLayoutManager);

        mReviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

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

        if (mImageView.getDrawable() != null) {
            Drawable background = mImageView.getDrawable().getConstantState().newDrawable();
            mBackgroundView.setBackground(background);
            mBackgroundView.getBackground().mutate().setAlpha(50);
        }

        mAverageVote.setText(String.valueOf(voteAverage) + "/10");
        mTitle.setText(originalTitle);
        mOverview.setText(overview);
        mReleaseDate.setText(releaseDate);


        Service apiService = Client.getClient().create(Service.class);
        Call<VideoResults> callVideos = apiService.getVideos(id.toString(), BuildConfig.API_KEY);
        callVideos.enqueue(new Callback<VideoResults>() {
            @Override
            public void onResponse(Call<VideoResults> call, Response<VideoResults> response) {
                List<Videos> trailers = response.body().getResults();
                if (trailers.size() != 0) {
                    mNoTrailersTV.setVisibility(View.INVISIBLE);
                    mTrailersAdapter = new TrailerAdapter(mContext, trailers, DetailActivity.this);
                    mTrailersRecyclerView.setAdapter(mTrailersAdapter);
                }
            }

            @Override
            public void onFailure(Call<VideoResults> call, Throwable t) {
                Log.e("retrofit videos", t.toString());
            }
        });

        Call<ReviewsResults> callReviews = apiService.getReviews(id.toString(), BuildConfig.API_KEY);
        callReviews.enqueue(new Callback<ReviewsResults>() {
            @Override
            public void onResponse(Call<ReviewsResults> call, Response<ReviewsResults> response) {
                List<Reviews> reviews = response.body().getResults();
                if (reviews.size() != 0) {
                    mNoReviewsTV.setVisibility(View.INVISIBLE);
                    mReviewsAdapter = new ReviewsAdapter(mContext, reviews);
                    mReviewsRecyclerView.setAdapter(mReviewsAdapter);
                }
            }

            @Override
            public void onFailure(Call<ReviewsResults> call, Throwable t) {
                Log.e("retrofit reviews", t.toString());
            }
        });

    }

    @Override
    public void onItemClick(Videos video) {
        String path = video.getKey();
        Intent intent = new Intent(DetailActivity.this, YoutubeActivity.class);
        intent.putExtra("KEY", path);
        startActivity(intent);
    }
}
