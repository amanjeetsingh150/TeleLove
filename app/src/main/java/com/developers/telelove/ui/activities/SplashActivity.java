package com.developers.telelove.ui.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.QuoteJobService;
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
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import static com.firebase.jobdispatcher.FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS;

public class SplashActivity extends AppCompatActivity {

    public static final String firstRun = "firstRun";
    private static final String TAG = SplashActivity.class.getSimpleName();
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;
    BallSpinFadeLoaderIndicator splashProgressBar;
    Observable<PopularPageResult> pageResultObservable;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
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
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder
                    (SplashActivity.this);
            dialogBuilder.setMessage("Do you want to receive quotes as notification?");
            dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseJobDispatcher dispatcher = new
                            FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
                    Job quoteJob = dispatcher.newJobBuilder()
                            .setService(QuoteJobService.class)
                            .setTag("quote")
                            .setRecurring(true)
                            .setTrigger(Trigger.executionWindow(60, 80))
                            .build();
                    dispatcher.schedule(quoteJob);
                    boolean s = dispatcher.schedule(quoteJob) == SCHEDULE_RESULT_SUCCESS;
                    Log.d(TAG, "Job scheduling" + s);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
            dialogBuilder.setNegativeButton("No", (dialogInterface, i) -> {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });
            dialogBuilder.show();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
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
