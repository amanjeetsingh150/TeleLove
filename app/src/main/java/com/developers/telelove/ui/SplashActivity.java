package com.developers.telelove.ui;

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.R;
import com.developers.telelove.data.ShowContract;
import com.developers.telelove.model.PopularShowsModel.PopularPageResult;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.model.VideosModel.VideoDetailResult;
import com.developers.telelove.model.VideosModel.VideoResult;
import com.developers.telelove.util.ApiInterface;
import com.developers.telelove.util.Constants;
import com.developers.telelove.util.FetchVideos;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {

    public static final String firstRun = "firstRun";
    public static final int FIRST_PAGE = 1;
    private static final String TAG = SplashActivity.class.getSimpleName();
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;
    @BindView(R.id.splash_progress_bar)
    ProgressBar splashProgressBar;
    int id;
    Observable<List<Result>> listObservable;
    Vector<ContentValues> vector = new Vector<>();
    private Observable<PopularPageResult> pageResultObservable;
    private List<Result> resultList;
    private List<VideoDetailResult> videoDetailResults;
    private Uri uri, backDropUri;
    private Observable<VideoResult> videoResultObservable;
    private String trailerUrl, trailer;
    private ContentValues popularShowsValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        ((App) getApplication()).getNetComponent().inject(this);
        if (sharedPreferences.getBoolean(firstRun, true)) {
            getPopularShowsFromApi(FIRST_PAGE);
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }


    public void getPopularShowsFromApi(int page) {
        id = 0;
        pageResultObservable = retrofit.create(ApiInterface.class)
                .getPopularShows(BuildConfig.TV_KEY, page);
        pageResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PopularPageResult>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(PopularPageResult popularPageResult) {
                        resultList = popularPageResult.getResults();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                        vector = new Vector<>(resultList.size());
                        for (Result result : resultList) {
                            popularShowsValues = new ContentValues();
                            popularShowsValues.put(ShowContract.PopularShows.COLUMN_ID
                                    , result.getId());
                            popularShowsValues.put(ShowContract.PopularShows.COLUMN_TITLE
                                    , result.getName());
                            uri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                                    .appendEncodedPath(result.getPosterPath())
                                    .build();
                            popularShowsValues.put(ShowContract.PopularShows.COLUMN_POSTER,
                                    uri.toString());
                            popularShowsValues.put(ShowContract.PopularShows.COLUMN_RELEASE_DATE,
                                    result.getFirstAirDate());
                            popularShowsValues.put(ShowContract.PopularShows.COLUMN_VOTE_AVERAGE,
                                    result.getVoteAverage());
                            popularShowsValues.put(ShowContract.PopularShows.COLUMN_OVERVIEW,
                                    result.getOverview());
                            backDropUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                                    .appendEncodedPath(result.getBackdropPath())
                                    .build();
                            Log.d(TAG, result.getName() + "Backdrop " + backDropUri.toString());
                            popularShowsValues.put(ShowContract.PopularShows.COLUMN_BACKDROP_IMG,
                                    backDropUri.toString());
                            fetchVideo(result.getId());
                            vector.add(popularShowsValues);
                        }
                        if (vector.size() > 0) {
                            ContentValues[] valueArray = new ContentValues[vector.size()];
                            valueArray = vector.toArray(valueArray);
                            getContentResolver().bulkInsert(ShowContract.PopularShows.uri, valueArray);
                        }
                        splashProgressBar.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
    }


    private void fetchVideo(int id) {
        new FetchVideos(videoResult -> {
            videoDetailResults = videoResult.getResults();
            if (videoDetailResults != null) {
                if (videoDetailResults.size() > 0) {
                    for (VideoDetailResult videoDetail : videoDetailResults) {
                        //when id has some value
                        Log.d(TAG, videoDetail.getKey() + " KEY");
                        if (videoDetail.getKey().length() != 0) {
                            Uri trailerUri = Uri.parse(Constants.YOUTUBE_BASE_URL)
                                    .buildUpon()
                                    .appendQueryParameter("v", videoDetail.getKey())
                                    .build();
                            trailerUrl = trailerUri.toString();
                            break;
                        }
                    }
                } else {
                    Log.d(TAG, "Not available");
                    trailerUrl = getString(R.string.trailer_not_available_error);
                }
                Log.d(TAG, "Trailer " + trailerUrl);
                popularShowsValues.put(ShowContract.PopularShows.COLUMN_TRAILER, trailerUrl);
            }
        }).execute(id);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().putBoolean(firstRun, false).apply();
    }
}
