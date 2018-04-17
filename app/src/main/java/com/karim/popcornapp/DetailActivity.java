package com.karim.popcornapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.karim.popcornapp.BuildConfig;
import com.example.karim.popcornapp.R;
import com.example.karim.popcornapp.databinding.DetailActivityBinding;
import com.karim.popcornapp.adapters.ReviewsAdapter;
import com.karim.popcornapp.adapters.TrailerAdapter;
import com.karim.popcornapp.api.Client;
import com.karim.popcornapp.api.Service;
import com.karim.popcornapp.data.Movies;
import com.karim.popcornapp.data.Reviews;
import com.karim.popcornapp.data.ReviewsResults;
import com.karim.popcornapp.data.VideoResults;
import com.karim.popcornapp.data.Videos;
import com.karim.popcornapp.persistence.FavoritesContract;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.karim.popcornapp.adapters.MovieAdapter.getPosterURI;

/**
 * Created by Karim on 17-Mar-18.
 */

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    DetailActivityBinding detailActivityBinding;
    private Context mContext;
    private TrailerAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private Integer movieId;
    private Double voteAverage;
    private String posterPath;
    private String originalTitle;
    private String englishTitle;
    private String overview;
    private String formattedDate;
    private String originalLanguage;

    private static final int LOADER_ID = 2;
    private Uri uri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        detailActivityBinding = DataBindingUtil.setContentView(this, R.layout.detail_activity);

        mContext = getApplicationContext();

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this);
        trailersLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        detailActivityBinding.trailersRecyclerView.setLayoutManager(trailersLayoutManager);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        detailActivityBinding.reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

        Intent intent = getIntent();
        Movies movie = intent.getParcelableExtra(getString(R.string.movie_details));

        //retrieving data from parcel
        movieId = movie.getId();
        uri = FavoritesContract.FavoritesEntry.CONTENT_URI.buildUpon().appendPath(movieId.toString()).build();
        voteAverage = movie.getVoteAverage();
        posterPath = movie.getPosterPath();
        originalTitle = movie.getOriginalTitle();
        englishTitle = movie.getTitle();
        overview = movie.getOverview();

        String releaseDate = movie.getReleaseDate();
        try {
            formattedDate = dateReformat(releaseDate);
        } catch (ParseException e) {
            formattedDate = releaseDate;
            Log.e("Date", e.getLocalizedMessage());
        }

        String lng = movie.getOriginalLanguage();
        Locale loc = new Locale(lng);
        originalLanguage = loc.getDisplayLanguage();

        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(englishTitle);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.mipmap.actionbar_icon);
        actionBar.setDisplayUseLogoEnabled(true);

        //loading poster
        Uri uri = getPosterURI(posterPath, getApplicationContext());
        Picasso.with(getApplicationContext()).load(uri).into(detailActivityBinding.imagePoster, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Drawable background = detailActivityBinding.imagePoster.getDrawable().getConstantState().newDrawable();
                detailActivityBinding.backgroundView.setBackground(background);
                detailActivityBinding.backgroundView.getBackground().mutate().setAlpha(50);
            }

            @Override
            public void onError() {
            }
        });

        detailActivityBinding.tvRating.setText(getString(R.string.rating, String.valueOf(voteAverage)));
        detailActivityBinding.tvTitle.setText(originalTitle);
        detailActivityBinding.tvOverview.setText(overview);
        detailActivityBinding.tvDate.setText(formattedDate);
        detailActivityBinding.tvLanguage.setText(originalLanguage);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        detailActivityBinding.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailActivityBinding.btnFav.isChecked())
                    addToFavorites();
                else removeFromFavorites();
                finish();
            }

        });


        Service apiService = Client.getClient().create(Service.class);
        Call<VideoResults> callVideos = apiService.getVideos(movieId.toString(), BuildConfig.API_KEY);
        callVideos.enqueue(new Callback<VideoResults>() {
            @Override
            public void onResponse(Call<VideoResults> call, Response<VideoResults> response) {
                List<Videos> trailers = response.body().getResults();
                if (trailers.size() != 0) {
                    detailActivityBinding.noTrailers.setVisibility(View.INVISIBLE);
                    mTrailersAdapter = new TrailerAdapter(mContext, trailers, DetailActivity.this);
                    detailActivityBinding.trailersRecyclerView.setAdapter(mTrailersAdapter);
                }
            }

            @Override
            public void onFailure(Call<VideoResults> call, Throwable t) {
                Log.e("retrofit videos", t.toString());
            }
        });

        Call<ReviewsResults> callReviews = apiService.getReviews(movieId.toString(), BuildConfig.API_KEY);
        callReviews.enqueue(new Callback<ReviewsResults>() {
            @Override
            public void onResponse(Call<ReviewsResults> call, Response<ReviewsResults> response) {
                List<Reviews> reviews = response.body().getResults();
                if (reviews.size() != 0) {
                    detailActivityBinding.noReviews.setVisibility(View.INVISIBLE);
                    mReviewsAdapter = new ReviewsAdapter(mContext, reviews);
                    detailActivityBinding.reviewsRecyclerView.setAdapter(mReviewsAdapter);
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

    private String dateReformat(String inputDateString) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = inputFormat.parse(inputDateString);
        return outputFormat.format(date);
    }

    public void addToFavorites() {
        ContentValues cv = new ContentValues();
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movieId);
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_ID, posterPath);
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE, englishTitle);
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_RATING, voteAverage);
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE, formattedDate);
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_LANGUAGE, originalLanguage);
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW, overview);

        Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, cv);
        if (uri != null) {
            Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromFavorites() {
        int removed = getContentResolver().delete(uri, null, null);
        if (removed > 0) {
            Toast.makeText(getApplicationContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uri, null, null, null, FavoritesContract.FavoritesEntry._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //check if there is data
        if (data != null && data.moveToFirst()) {
            detailActivityBinding.btnFav.setChecked(true);
        } else detailActivityBinding.btnFav.setChecked(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //not applicable
    }

}
