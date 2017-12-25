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
    public static final String CREATE_POPULAR_TABLE = "CREATE TABLE " + ShowContract.PopularShows.TABLE_NAME
            + " (" + ShowContract.PopularShows._ID + " INTEGER PRIMARY KEY, " +
            ShowContract.PopularShows.COLUMN_ID + " INTEGER NOT NULL, " +
            ShowContract.PopularShows.COLUMN_TITLE + " TEXT NOT NULL, " +
            ShowContract.PopularShows.COLUMN_POSTER + " TEXT NOT NULL, " +
            ShowContract.PopularShows.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            ShowContract.PopularShows.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
            ShowContract.PopularShows.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            ShowContract.PopularShows.COLUMN_TRAILER + " TEXT," +
            ShowContract.PopularShows.COLUMN_BACKDROP_IMG + " TEXT NOT NULL" + ")";

    public ShowsOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_POPULAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + ShowContract.PopularShows.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
