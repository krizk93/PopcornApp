package com.example.karim.popcornapp.api;

import com.example.karim.popcornapp.data.MovieResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Karim on 17-Mar-18.
 */

public interface Service {
    @GET("/3/movie/{category}")
    Call<MovieResults> getMovies(
            @Path("category") String category,
            @Query("api_key") String apiKey
    );
}
