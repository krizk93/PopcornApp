package com.example.karim.popcornapp.api;

import com.example.karim.popcornapp.data.MovieResults;
import com.example.karim.popcornapp.data.ReviewsResults;
import com.example.karim.popcornapp.data.VideoResults;

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

    @GET("/3/movie/{id}/videos")
    Call<VideoResults> getVideos(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET("/3/movie/{id}/reviews")
    Call<ReviewsResults> getReviews(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );
}
