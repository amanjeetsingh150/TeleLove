package com.developers.telelove.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.developers.telelove.App;
import com.developers.telelove.R;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((App) getApplication()).getNetComponent().inject(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container,
                    new MainFragment()).commit();
        }
    }
}
