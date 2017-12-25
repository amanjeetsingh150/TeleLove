package com.developers.telelove.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class ShowsProvider extends ContentProvider {


    private static final int POPULAR = 1;
    private static final int POPULAR_WITH_ID = 2;
    private ShowsOpenHelper showsOpenHelper;
    private UriMatcher uriMatcher = buildUriMatcher();

    public ShowsProvider() {
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ShowContract.CONTENT_AUTHORITY, ShowContract.PATH_POPULAR, POPULAR);
        uriMatcher.addURI(ShowContract.CONTENT_AUTHORITY, ShowContract.PATH_POPULAR + "/*", POPULAR_WITH_ID);
        return uriMatcher;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = showsOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)) {
            case POPULAR:
                rowsDeleted = database.delete(ShowContract.PopularShows.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR_WITH_ID:
                rowsDeleted = database.delete(ShowContract.PopularShows.TABLE_NAME,
                        ShowContract.PopularShows.COLUMN_ID + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case POPULAR:
                return ShowContract.PopularShows.CONTENT_TYPE;
            case POPULAR_WITH_ID:
                return ShowContract.PopularShows.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase database = showsOpenHelper.getWritableDatabase();
        Uri returnUri = null;
        long id;
        switch (uriMatcher.match(uri)) {
            case POPULAR:
                id = database.insert(ShowContract.PopularShows.TABLE_NAME, null, values);
                if (id == -1) {
                    throw new SQLException("Error In insertion");
                }
                returnUri = ShowContract.PopularShows.buildPopularShowsUri(id);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        showsOpenHelper = new ShowsOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor mCursor;
        switch (uriMatcher.match(uri)) {
            case POPULAR:
                mCursor = showsOpenHelper.getReadableDatabase().query(ShowContract.PopularShows.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                mCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return mCursor;
            case POPULAR_WITH_ID:
                mCursor = showsOpenHelper.getReadableDatabase().query(ShowContract.PopularShows.TABLE_NAME,
                        projection, ShowContract.PopularShows.COLUMN_ID + " =? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null, null, sortOrder);
                mCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return mCursor;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int numUpdated = 0;
        switch (uriMatcher.match(uri)) {
            case POPULAR:
                numUpdated = showsOpenHelper.getWritableDatabase().update(ShowContract.PopularShows.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case POPULAR_WITH_ID:
                numUpdated = showsOpenHelper.getWritableDatabase().update(ShowContract.PopularShows.TABLE_NAME,
                        values, ShowContract.PopularShows.COLUMN_ID + " =? "
                        , new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if (numUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase database = showsOpenHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case POPULAR:
                int count = 0;
                database.beginTransaction();
                try {
                    for (ContentValues contentValues : values) {
                        long id = database.insert(ShowContract.PopularShows.TABLE_NAME,
                                null, contentValues);
                        if (id == -1) {
                            count++;
                        }
                    }
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
    }
}
