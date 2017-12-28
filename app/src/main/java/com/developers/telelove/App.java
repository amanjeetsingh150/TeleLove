package com.developers.telelove;

import android.app.Application;

import com.developers.telelove.di.component.DaggerNetComponent;
import com.developers.telelove.di.component.NetComponent;
import com.developers.telelove.di.module.AppModule;
import com.developers.telelove.di.module.NetModule;
import com.developers.telelove.util.Constants;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Amanjeet Singh on 23/12/17.
 */

public class App extends Application {

    public static Picasso picassoWithCache;
    NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        netComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(Constants.BASE_URL))
                .build();
        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);
        picassoWithCache = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClientBuilder.build())).build();
        Picasso.setSingletonInstance(picassoWithCache);
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }
}
