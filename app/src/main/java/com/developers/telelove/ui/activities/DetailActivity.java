package com.developers.telelove.ui.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.developers.telelove.App;
import com.developers.telelove.R;
import com.developers.telelove.ui.DetailsFragment;
import com.developers.telelove.util.Constants;

import javax.inject.Inject;

import butterknife.BindView;

public class DetailActivity extends AppCompatActivity {

    String detailsJson, preference;
    @Inject
    SharedPreferences sharedPreferences;
    private DetailsFragment detailsFragment;
    @BindView(R.id.toolbar_details)
    Toolbar detailsToolBa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ((App) getApplication()).getNetComponent().inject(this);
        setSupportActionBar(detailsToolBa);
        if (savedInstanceState == null) {
            preference = sharedPreferences.getString(getString(R.string.preferences_key), "0");
            switch (preference) {
                case "0":
                    detailsJson = getIntent().getStringExtra(Constants.KEY_POPULAR_SHOWS);
                    break;
                case "1":
                    detailsJson = getIntent().getStringExtra(Constants.KEY_TOP_RATED);
                    break;
                case "2":
                    detailsJson = getIntent().getStringExtra(Constants.KEY_FAVOURITES);
            }
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_DETAILS, detailsJson);
            detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, detailsFragment).commit();
        }
    }
}
