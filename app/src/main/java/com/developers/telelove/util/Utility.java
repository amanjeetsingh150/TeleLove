package com.developers.telelove.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.developers.telelove.model.PopularShowsModel.Result;

/**
 * Created by Amanjeet Singh on 24/12/17.
 */

public class Utility {

    public static int PAGE_ONE_SHOWS_SIZE = 0;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public interface ClickCallBacks {
        void onClick(Result result, int position);
    }

}
