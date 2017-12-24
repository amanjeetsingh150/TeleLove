package com.developers.telelove.util;

import com.developers.telelove.model.PopularShowsModel.PopularPageResult;
import com.developers.telelove.model.VideosModel.VideoResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Amanjeet Singh on 23/12/17.
 */

public interface ApiInterface {


    @GET("popular")
    Observable<PopularPageResult> getPopularShows(@Query("api_key") String apiKey,
                                                  @Query("page") int page);

    @GET("{tv_id}/videos")
    Observable<VideoResult> getTrailers(@Path("tv_id") int id,
                                        @Query("api_key") String apiKey);


}
