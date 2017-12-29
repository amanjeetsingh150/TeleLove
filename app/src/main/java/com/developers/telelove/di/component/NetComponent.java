package com.developers.telelove.di.component;

import com.developers.telelove.di.module.AppModule;
import com.developers.telelove.di.module.NetModule;
import com.developers.telelove.ui.DetailsFragment;
import com.developers.telelove.ui.activities.DetailActivity;
import com.developers.telelove.ui.activities.MainActivity;
import com.developers.telelove.ui.MainFragment;
import com.developers.telelove.ui.activities.SettingsActivity;
import com.developers.telelove.ui.activities.SplashActivity;
import com.developers.telelove.util.FetchVideos;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Amanjeet Singh on 23/12/17.
 */
@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {

    void inject(MainActivity mainActivity);

    void inject(SplashActivity splashActivity);

    void inject(MainFragment mainFragment);

    void inject(DetailsFragment detailsFragment);

    void inject(FetchVideos fetchVideos);

    void inject(SettingsActivity.PrefFrag prefFrag);

    void inject(DetailActivity detailActivity);

}
