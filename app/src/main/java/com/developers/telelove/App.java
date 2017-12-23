package com.developers.telelove;

import android.app.Application;

import com.developers.telelove.di.component.DaggerNetComponent;
import com.developers.telelove.di.component.NetComponent;
import com.developers.telelove.di.module.AppModule;
import com.developers.telelove.di.module.NetModule;
import com.developers.telelove.util.Constants;

/**
 * Created by Amanjeet Singh on 23/12/17.
 */

public class App extends Application {

    NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        netComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(Constants.BASE_URL))
                .build();
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }
}
