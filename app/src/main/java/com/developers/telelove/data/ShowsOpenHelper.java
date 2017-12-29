package com.developers.telelove.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Amanjeet Singh on 24/12/17.
 */

public class ShowsOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "shows.db";
    public static final int DB_VERSION = 2;
    public static final String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + ShowContract.FavouriteShows.TABLE_NAME
            + " (" + ShowContract.FavouriteShows._ID + " INTEGER PRIMARY KEY, " +
            ShowContract.FavouriteShows.COLUMN_ID + " TEXT NOT NULL, " +
            ShowContract.FavouriteShows.COLUMN_TITLE + " TEXT NOT NULL, " +
            ShowContract.FavouriteShows.COLUMN_POSTER + " TEXT NOT NULL, " +
            ShowContract.FavouriteShows.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            ShowContract.FavouriteShows.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
            ShowContract.FavouriteShows.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            ShowContract.FavouriteShows.COLUMN_TRAILER + " TEXT," +
            ShowContract.FavouriteShows.COLUMN_BACKDROP_IMG + " TEXT NOT NULL," +
            ShowContract.FavouriteShows.COLUMN_SIMILAR_SHOWS + " TEXT, " +
            ShowContract.FavouriteShows.COLUMN_CHARACTERS + " TEXT" + ")";


    public ShowsOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + ShowContract.FavouriteShows.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
