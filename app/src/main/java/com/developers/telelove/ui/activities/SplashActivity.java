package com.developers.telelove.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.R;
import com.developers.telelove.data.ShowContract;
import com.developers.telelove.data.ShowsOpenHelper;
import com.developers.telelove.events.LaunchMessageEvent;
import com.developers.telelove.model.PopularShowsModel.PopularPageResult;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedDetailResults;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedResults;
import com.developers.telelove.model.VideosModel.VideoDetailResult;
import com.developers.telelove.util.ApiInterface;
import com.developers.telelove.util.Constants;
import com.developers.telelove.util.FetchVideos;
import com.developers.telelove.util.Utility;
import com.google.gson.Gson;
import com.wang.avi.indicators.BallSpinFadeLoaderIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Vector;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    BallSpinFadeLoaderIndicator splashProgressBar;
    Observable<PopularPageResult> pageResultObservable;
    private List<Result> resultList;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashProgressBar = new BallSpinFadeLoaderIndicator();
        ButterKnife.bind(this);
        ((App) getApplication()).getNetComponent().inject(this);
        if (sharedPreferences.getBoolean(firstRun, true)) {
            getPopularShowsFromApi(FIRST_PAGE);
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }


    public void getPopularShowsFromApi(int page) {
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
                        splashProgressBar.setVisible(false, false);
                        gson = new Gson();
                        String resultJson = gson.toJson(resultList);
                        Log.d(TAG,resultJson);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(Constants.KEY_POPULAR_SHOWS, resultJson);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().putBoolean(firstRun, false).apply();
    }
}
