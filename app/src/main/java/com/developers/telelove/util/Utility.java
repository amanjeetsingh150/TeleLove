package com.developers.telelove.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.developers.telelove.model.PopularShowsModel.Result;

/**
 * Created by Amanjeet Singh on 24/12/17.
 */

public class Utility {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public static boolean validateUriForAppending(String path) {
        Character character = path.charAt(0);
        if (character.equals('/')) {
            return true;
        }
        return false;
    }

    public interface ClickCallBacks {
        void onClick(Result result,int position);
    }

    public interface onComplete{
        void onResult(String results);
    }

}
