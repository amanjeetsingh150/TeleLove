package com.developers.telelove.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.developers.telelove.BuildConfig;
import com.developers.telelove.model.VideosModel.VideoDetailResult;
import com.developers.telelove.model.VideosModel.VideoResult;
import com.google.gson.Gson;

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

public class FetchVideos extends AsyncTask<Integer, Integer, List<VideoDetailResult>> {

    private static final String TAG = FetchVideos.class.getSimpleName();
    OnRequestFinish onRequestFinish;

    public FetchVideos(OnRequestFinish onRequestFinish) {
        this.onRequestFinish = onRequestFinish;
    }

    @Override
    protected List<VideoDetailResult> doInBackground(Integer... id) {
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
            StringBuilder sb = new StringBuilder();
            String rs;
            while ((rs = buff.readLine()) != null) {
                sb.append(rs);
            }
            Gson gson = new Gson();
            VideoResult videoResult = gson.fromJson(sb.toString(), VideoResult.class);
            onRequestFinish.onFinish(videoResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnRequestFinish {
        void onFinish(VideoResult videoResult);
    }


}
