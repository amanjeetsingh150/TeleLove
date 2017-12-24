package com.developers.telelove.di.component;

import com.developers.telelove.di.module.AppModule;
import com.developers.telelove.di.module.NetModule;
import com.developers.telelove.ui.MainActivity;
import com.developers.telelove.ui.SplashActivity;

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

}
