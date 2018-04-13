package com.karim.popcornapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karim.popcornapp.BuildConfig;
import com.example.karim.popcornapp.R;
import com.karim.popcornapp.adapters.MovieAdapter;
import com.karim.popcornapp.api.Client;
import com.karim.popcornapp.api.Service;
import com.karim.popcornapp.data.MovieResults;
import com.karim.popcornapp.data.Movies;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.PosterItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPreferences;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mLoadingTextView;
    private Button mRefreshButton;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mProgressBar = findViewById(R.id.progress_bar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLoadingTextView = findViewById(R.id.tv_loading);
        mRefreshButton = findViewById(R.id.refresh_button);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recycler_view);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mLoadingTextView.setVisibility(View.VISIBLE);
        setNavigationViewListener();
        loadData();


    }

    private void loadData() {
        if (BuildConfig.API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "NO API KEY FOUND", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.INVISIBLE);
            mLoadingTextView.setVisibility(View.INVISIBLE);
            return;
        }
        Service apiService = Client.getClient().create(Service.class);
        if (!isOnline()) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mLoadingTextView.setText(getString(R.string.no_internet));
            mRefreshButton.setVisibility(View.VISIBLE);
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadData();
                }
            });
        } else {
            //get category from shared preferences, default is popular
            sharedPreferences = getSharedPreferences("Movie Categories", Context.MODE_PRIVATE);
            String category = sharedPreferences.getString("category", "popular");
            setActionBarTitle(category);
            Call<MovieResults> call = apiService.getMovies(category, BuildConfig.API_KEY);
            call.enqueue(new Callback<MovieResults>() {
                @Override
                public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                    List<Movies> movies = response.body().getResults();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLoadingTextView.setVisibility(View.INVISIBLE);
                    mRefreshButton.setVisibility(View.INVISIBLE);
                    mAdapter = new MovieAdapter(mContext, movies, MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);

                }

                @Override
                public void onFailure(Call<MovieResults> call, Throwable t) {
                    Log.e("retrofit", t.toString());
                }
            });
        }
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setActionBarTitle(String criteria) {
        switch (criteria) {
            case "popular":
                getSupportActionBar().setTitle(getString(R.string.popular_movies));
                break;
            case "top_rated":
                getSupportActionBar().setTitle(getString(R.string.top_rated_movies));
                break;
        }
    }


    @Override
    public void onItemClick(Movies movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(getString(R.string.movie_details), movie);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (item.getItemId()) {
            case R.id.popular:
                editor.putString("category", "popular");
                editor.apply();
                setActionBarTitle("popular");
                getSupportActionBar().setTitle(getString(R.string.popular_movies));
                loadData();
                break;
            case R.id.top_rated:
                editor.putString("category", "top_rated");
                editor.apply();
                setActionBarTitle("top_rated");
                loadData();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
