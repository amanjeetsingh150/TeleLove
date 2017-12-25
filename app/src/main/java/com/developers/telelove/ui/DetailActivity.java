package com.developers.telelove.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.developers.telelove.R;
import com.developers.telelove.util.Constants;

public class DetailActivity extends AppCompatActivity {

    String detailsJson;
    private DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            detailsJson = getIntent().getStringExtra(Constants.KEY_POPULAR_SHOWS);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_DETAILS, detailsJson);
            detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, detailsFragment).commit();
        }
    }
}
