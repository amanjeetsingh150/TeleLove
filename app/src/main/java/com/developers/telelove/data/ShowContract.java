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
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "rated";
    public static final String TAG = ShowContract.class.getSimpleName();
    public static final String PATH_FAVOUR = "favourite";

    public static final class PopularShows implements BaseColumns {
        public static final Uri uri = CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String TABLE_NAME = "popular";
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
                {PopularShows._ID, PopularShows.COLUMN_ID, PopularShows.COLUMN_POSTER,
                        PopularShows.COLUMN_TITLE, PopularShows.COLUMN_RELEASE_DATE,
                        PopularShows.COLUMN_VOTE_AVERAGE, PopularShows.COLUMN_OVERVIEW,
                        PopularShows.COLUMN_TRAILER, PopularShows.COLUMN_BACKDROP_IMG};


        public static Uri buildPopularShowsUri(long popularId) {
            return ContentUris.withAppendedId(CONTENT_URI, popularId);
        }
    }

}
