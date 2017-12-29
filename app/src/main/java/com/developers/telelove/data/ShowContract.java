package com.developers.telelove.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Amanjeet Singh on 23/12/17.
 */

public class ShowContract {

    public static final String CONTENT_AUTHORITY = "com.developers.telelove";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVOURITE = "favourite";
    public static final String TAG = ShowContract.class.getSimpleName();

    public static final class FavouriteShows implements BaseColumns {
        public static final Uri uri = CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;
        public static final String TABLE_NAME = "favourite";
        public static final String COLUMN_ID = "show_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RELEASE_DATE = "date";
        public static final String COLUMN_VOTE_AVERAGE = "average";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_BACKDROP_IMG = "backdrop";
        public static final String COLUMN_SIMILAR_SHOWS = "similar_shows";
        public static final String COLUMN_CHARACTERS = "characters";
        public static final String[] projectionsForMainActivity =
                {FavouriteShows._ID, FavouriteShows.COLUMN_ID, FavouriteShows.COLUMN_POSTER,
                        FavouriteShows.COLUMN_TITLE, FavouriteShows.COLUMN_RELEASE_DATE,
                        FavouriteShows.COLUMN_VOTE_AVERAGE, FavouriteShows.COLUMN_OVERVIEW,
                        FavouriteShows.COLUMN_TRAILER, FavouriteShows.COLUMN_BACKDROP_IMG};


        public static Uri buildFavouriteShowsUri(long favourId) {
            return ContentUris.withAppendedId(CONTENT_URI, favourId);
        }
    }

}
