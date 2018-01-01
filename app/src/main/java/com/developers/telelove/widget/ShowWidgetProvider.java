package com.developers.telelove.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.developers.telelove.App;
import com.developers.telelove.R;
import com.developers.telelove.model.FavouriteShowsResult;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedDetailResults;
import com.developers.telelove.util.Constants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

/**
 * Implementation of App Widget functionality.
 */
public class ShowWidgetProvider extends AppWidgetProvider {

    String preference;
    Gson gson;

    public static void updateAppWidgetWithPopularShow(Context context,
                                                      AppWidgetManager appWidgetManager,
                                                      int appWidgetId, Result result) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.show_widget_provider);
        views.setTextViewText(R.id.title_widget_text_view, "Title: " + result.getName());
        views.setTextViewText(R.id.release_date_widget_text_view, "Release: " +
                result.getFirstAirDate());
        views.setTextViewText(R.id.rating_widget_text_view, "Rating: " +
                String.valueOf(result.getVoteAverage()));
        Uri posterUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                .appendEncodedPath(result.getPosterPath()).build();
        Picasso.with(context).load(posterUri).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                views.setImageViewBitmap(R.id.poster_image_widget, bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("Widget", "Error in loading");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgetWithRatedShow(Context context,
                                                    AppWidgetManager appWidgetManager,
                                                    int appWidgetId,
                                                    TopRatedDetailResults ratedDetailResults) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.show_widget_provider);
        views.setTextViewText(R.id.title_widget_text_view, "Title: " +
                ratedDetailResults.getName());
        views.setTextViewText(R.id.release_date_widget_text_view, "Release Date: " +
                ratedDetailResults.getFirstAirDate());
        views.setTextViewText(R.id.rating_widget_text_view,
                "Rating: " + String.valueOf(ratedDetailResults.getVoteAverage()));
        Uri posterUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                .appendEncodedPath(ratedDetailResults.getPosterPath()).build();
        Picasso.with(context).load(posterUri).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                views.setImageViewBitmap(R.id.poster_image_widget, bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("Widget", "Error in loading");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgetWithFavouriteShow(Context context,
                                                        AppWidgetManager appWidgetManager,
                                                        int appWidgetId,
                                                        FavouriteShowsResult result) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.show_widget_provider);
        views.setTextViewText(R.id.title_widget_text_view, "Title: " +
                result.getTitle());
        views.setTextViewText(R.id.release_date_widget_text_view, "Release: " +
                result.getReleaseDate());
        views.setTextViewText(R.id.rating_widget_text_view, "Rating: " + result.getRating());
        Picasso.with(context).load(result.getPosterPath()).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                views.setImageViewBitmap(R.id.poster_image_widget, bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("Widget", "Error in loading");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        SharedPreferences shared = context
                .getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        preference = shared.getString("order", "0");
        gson = new Gson();
        switch (preference) {
            case "0":
                String popular = shared.getString(Constants.KEY_WIDGET_JSON, null);
                Result result = gson.fromJson(popular, Result.class);
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidgetWithPopularShow(context, appWidgetManager, appWidgetId, result);
                }
                break;
            case "1":
                String rated = shared.getString(Constants.KEY_WIDGET_JSON, null);
                TopRatedDetailResults ratedResult = gson.fromJson(rated, TopRatedDetailResults.class);
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidgetWithRatedShow(context, appWidgetManager, appWidgetId, ratedResult);
                }
                break;
            case "2":
                String favourite = shared.getString(Constants.KEY_WIDGET_JSON, null);
                FavouriteShowsResult favouriteShowsResult = gson.fromJson(favourite,
                        FavouriteShowsResult.class);
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidgetWithFavouriteShow(context, appWidgetManager, appWidgetId, favouriteShowsResult);
                }
                break;
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

