package com.developers.telelove.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.developers.telelove.App;
import com.developers.telelove.R;
import com.developers.telelove.ui.MainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toolBar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        ((App) getApplication()).getNetComponent().inject(this);
        boolean isTabUsed = getResources().getBoolean(R.bool.tab);
        if(!isTabUsed){
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container,
                        new MainFragment()).commit();
            }
        }
    }
}
