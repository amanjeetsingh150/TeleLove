package com.developers.telelove.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.developers.telelove.App;
import com.developers.telelove.service.QuoteJobService;
import com.developers.telelove.R;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import com.wang.avi.indicators.BallSpinFadeLoaderIndicator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import retrofit2.Retrofit;

import static com.firebase.jobdispatcher.FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS;

public class SplashActivity extends AppCompatActivity {

    public static final String firstRun = "firstRun";
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static int periodicityForOneDay = 86400;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;
    BallSpinFadeLoaderIndicator splashProgressBar;
    @Inject
    FirebaseJobDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashProgressBar = new BallSpinFadeLoaderIndicator();
        ButterKnife.bind(this);
        ((App) getApplication()).getNetComponent().inject(this);
        if (sharedPreferences.getBoolean(firstRun, true)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder
                            (SplashActivity.this);
                    dialogBuilder.setIcon(R.drawable.tv_red);
                    dialogBuilder.setMessage(getString(R.string.splash_dialog_message));
                    dialogBuilder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        Job quoteJob = dispatcher.newJobBuilder()
                                .setService(QuoteJobService.class)
                                .setTag(getString(R.string.job_tag))
                                .setRecurring(true)
                                .setTrigger(Trigger.executionWindow(periodicityForOneDay,
                                        periodicityForOneDay + 30))
                                .build();
                        dispatcher.schedule(quoteJob);
                        boolean s = dispatcher.schedule(quoteJob) == SCHEDULE_RESULT_SUCCESS;
                        if(s){
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.quotes_scheduled), Toast.LENGTH_SHORT).show();
                        }
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    });
                    dialogBuilder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    });
                    dialogBuilder.show();
                }
            }, 3000);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
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
        finish();
    }
}
