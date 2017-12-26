package com.developers.telelove.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.events.LaunchMessageEvent;
import com.developers.telelove.model.VideosModel.VideoDetailResult;
import com.developers.telelove.model.VideosModel.VideoResult;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Amanjeet Singh on 26/12/17.
 */

public class FetchVideos extends AsyncTask<Integer, Integer, String> {

    private static final String TAG = FetchVideos.class.getSimpleName();
    private static int current_size = 0;
    private OnRequestFinish onRequestFinish;
    private StringBuilder sb;

    public FetchVideos(OnRequestFinish onRequestFinish) {
        this.onRequestFinish = onRequestFinish;

    }

    @Override
    protected String doInBackground(Integer... id) {
        try {
            Uri trailerUri = Uri.parse(Constants.BASE_URL).buildUpon()
                    .appendPath(String.valueOf(id[0]))
                    .appendPath("videos")
                    .appendQueryParameter("api_key", BuildConfig.TV_KEY).build();
            Log.d(TAG, trailerUri.toString());
            URL url = new URL(trailerUri.toString());
            HttpURLConnection con1 = (HttpURLConnection) url.openConnection();
            InputStream is = con1.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuilder();
            String rs;
            while ((rs = buff.readLine()) != null) {
                sb.append(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String sb) {
        super.onPostExecute(sb);
        Gson gson = new Gson();
        VideoResult videoResult = gson.fromJson(sb, VideoResult.class);
        onRequestFinish.onFinish(videoResult);
        current_size++;
        Log.d(TAG, current_size + " WITHHHHHHH " + Utility.PAGE_ONE_SHOWS_SIZE);
        if (Utility.PAGE_ONE_SHOWS_SIZE  == current_size) {
            //issue a broadcast that activity ready to launch
            Log.d(TAG, " Brrrrrrrrrrr");
            LaunchMessageEvent launchMessageEvent = new LaunchMessageEvent();
            launchMessageEvent.setShouldLaunch(true);
            EventBus.getDefault().post(launchMessageEvent);
        }
    }

    public interface OnRequestFinish {
        void onFinish(VideoResult videoResult);
    }


}
