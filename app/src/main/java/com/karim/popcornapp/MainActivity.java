package com.karim.popcornapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.example.karim.popcornapp.databinding.ActivityMainBinding;
import com.karim.popcornapp.adapters.MovieAdapter;
import com.karim.popcornapp.api.Client;
import com.karim.popcornapp.api.Service;
import com.karim.popcornapp.data.MovieResults;
import com.karim.popcornapp.data.Movies;
import com.karim.popcornapp.persistence.FavoritesContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.PosterItemClickListener,
        NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {


    SharedPreferences sharedPreferences;
    private Context mContext;
    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActionBarDrawerToggle mToggle;
    private static final int LOADER_ID = 0;
    ActivityMainBinding mainBinding;
    private static final String LIST_STATE_KEY = "state_key";
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        mToggle = new ActionBarDrawerToggle(this, mainBinding.drawerLayout, R.string.open, R.string.close);
        mainBinding.drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(mContext, 2);
        } else {
            mLayoutManager = new GridLayoutManager(mContext, 3);
        }
        mainBinding.recyclerView.setLayoutManager(mLayoutManager);
        setNavigationViewListener();
        //loadData();


    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);

    }

    private void loadData() {
        mainBinding.progressBar.setVisibility(View.VISIBLE);
        mainBinding.tvLoading.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences("Movie Categories", Context.MODE_PRIVATE);
        String category = sharedPreferences.getString("category", "popular");

        mAdapter = new MovieAdapter(this, MainActivity.this);
        mainBinding.recyclerView.setAdapter(mAdapter);
        mAdapter.swapCursor(null);

        if (BuildConfig.API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "NO API KEY FOUND", Toast.LENGTH_LONG).show();
            mainBinding.progressBar.setVisibility(View.INVISIBLE);
            mainBinding.tvLoading.setVisibility(View.INVISIBLE);
            return;
        }
        Service apiService = Client.getClient().create(Service.class);
        if (!isOnline()) {
            mainBinding.progressBar.setVisibility(View.INVISIBLE);
            mainBinding.tvLoading.setText(getString(R.string.no_internet));
            mainBinding.refreshButton.setVisibility(View.VISIBLE);

            mainBinding.refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadData();
                }
            });

        } else {
            setActionBarTitle(category);
            if (category.equals("favorites")) {
                loadFromDatabase();
                return;
            }
            Call<MovieResults> call = apiService.getMovies(category, BuildConfig.API_KEY);
            call.enqueue(new Callback<MovieResults>() {
                @Override
                public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                    List<Movies> movies = response.body().getResults();
                    mainBinding.progressBar.setVisibility(View.INVISIBLE);
                    mainBinding.tvLoading.setVisibility(View.INVISIBLE);
                    mainBinding.refreshButton.setVisibility(View.INVISIBLE);
                    mAdapter = new MovieAdapter(mContext, movies, MainActivity.this);
                    mainBinding.recyclerView.setAdapter(mAdapter);
                    if (mListState != null)
                        mLayoutManager.onRestoreInstanceState(mListState);

                }

                @Override
                public void onFailure(Call<MovieResults> call, Throwable t) {
                    Log.e("retrofit", t.toString());
                }
            });
        }
    }

    private void setNavigationViewListener() {
        mainBinding.navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setActionBarTitle(String criteria) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        switch (criteria) {
            case "popular":
                actionBar.setTitle(getString(R.string.popular_movies));
                break;
            case "top_rated":
                actionBar.setTitle(getString(R.string.top_rated_movies));
                break;

            case "favorites":
                actionBar.setTitle(getString(R.string.favourite_movies));
                break;

            case "now_playing":
                actionBar.setTitle(getString(R.string.now_playing_movies));
                break;
            case "upcoming":
                actionBar.setTitle(getString(R.string.upcoming_movies));

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
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (item.getItemId()) {
            case R.id.popular:
                editor.putString("category", "popular");
                editor.apply();
                setActionBarTitle("popular");
                loadData();
                break;
            case R.id.top_rated:
                editor.putString("category", "top_rated");
                editor.apply();
                setActionBarTitle("top_rated");
                loadData();
                break;

            case R.id.favourites:
                editor.putString("category", "favorites");
                editor.apply();
                setActionBarTitle("favorites");
                loadFromDatabase();
                break;

            case R.id.now_playing:
                editor.putString("category", "now_playing");
                editor.apply();
                setActionBarTitle("now_playing");
                loadData();
                break;

            case R.id.upcoming:
                editor.putString("category", "upcoming");
                editor.apply();
                setActionBarTitle("upcoming");
                loadData();
                break;
        }
        mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFromDatabase() {
        mainBinding.progressBar.setVisibility(View.INVISIBLE);
        mainBinding.tvLoading.setVisibility(View.INVISIBLE);
        mainBinding.refreshButton.setVisibility(View.INVISIBLE);
        mAdapter = new MovieAdapter(mContext, MainActivity.this);
        mainBinding.recyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, FavoritesContract.FavoritesEntry.CONTENT_URI, null, null, null, FavoritesContract.FavoritesEntry._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
