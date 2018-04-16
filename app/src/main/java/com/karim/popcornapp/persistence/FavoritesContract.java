package com.karim.popcornapp.persistence;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Karim on 13-Apr-18.
 */

public class FavoritesContract {

    private FavoritesContract() {
    }

    public static final String AUTHORITY = "com.karim.popcornapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";


    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_POSTER_ID = "posterId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_OVERVIEW = "overview";
    }
}
