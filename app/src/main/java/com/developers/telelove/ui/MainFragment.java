package com.developers.telelove.ui;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.R;
import com.developers.telelove.adapters.PopularTvShowsAdapter;
import com.developers.telelove.data.ShowContract;
import com.developers.telelove.data.ShowsOpenHelper;
import com.developers.telelove.model.CharactersModel.Cast;
import com.developers.telelove.model.CharactersModel.CharacterResult;
import com.developers.telelove.model.PopularShowsModel.PopularPageResult;
import com.developers.telelove.model.PopularShowsModel.PopularResultData;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowDetails;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowResults;
import com.developers.telelove.model.VideosModel.VideoDetailResult;
import com.developers.telelove.model.VideosModel.VideoResult;
import com.developers.telelove.util.ApiInterface;
import com.developers.telelove.util.Constants;
import com.developers.telelove.util.PaginationScrollListener;
import com.developers.telelove.util.Utility;
import com.google.gson.Gson;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, Utility.ClickCallBacks {


    private static final int START_PAGE = 1;
    private static final int SHOWS_LOADER = 2;
    private static final String TAG = MainFragment.class.getSimpleName();
    @BindView(R.id.shows_recycler_view)
    RecyclerView showsRecyclerView;
    @BindView(R.id.progress_bar_grid)
    ProgressBar progressBar;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    Observable<PopularPageResult> pageResultObservable;
    PaginationScrollListener scrollListener;
    LinearLayoutManager linearLayoutManager;
    List<Result> results;
    String popularShowsJson, resultJson, similarShowsResult, characterJson;
    Gson gson;
    int last;
    @BindView(R.id.frame_layout_list)
    FrameLayout frameLayout;
    Observable<VideoResult> videoResultObservable;
    List<VideoDetailResult> videoDetailResults;
    private PopularTvShowsAdapter popularTvShowsAdapter;
    private List<Result> resultList = new ArrayList<>();
    private List<PopularResultData> popularResultList = new ArrayList<>();
    private String trailer;
    private List<Cast> castList, castDetailResult;
    private List<SimilarShowDetails> similarShowDetails, similarShowDetailInfo;
    private PopularResultData popularResultData;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        ((App) getActivity().getApplication()).getNetComponent().inject(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        initAdapter(resultList, 1);
        showsRecyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (Utility.isNetworkConnected(getActivity())) {
                    getPopularShowsFromApi(current_page);
                } else {
                    Log.d(TAG, "No internet");
                    showLoadMoreSpinner();
                }
            }
        };
        showsRecyclerView.addOnScrollListener(scrollListener);
        getActivity().getSupportLoaderManager().initLoader(SHOWS_LOADER, null, this);
        return view;
    }

    private void initAdapter(List<Result> results, int page) {
        if (results != null) {
            popularTvShowsAdapter = new PopularTvShowsAdapter(getActivity(),
                    results, page);
            popularTvShowsAdapter.setClickCallBacks(this);
            showsRecyclerView.setAdapter(popularTvShowsAdapter);
            last = linearLayoutManager.getItemCount();
        }
    }

    public void getPopularShowsFromApi(int page) {
        if (page > 1) {
            showLoadMoreSpinner();
        }
        pageResultObservable = retrofit.create(ApiInterface.class)
                .getPopularShows(BuildConfig.TV_KEY, page);
        pageResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PopularPageResult>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(PopularPageResult popularPageResult) {
                        resultList = popularPageResult.getResults();
                        hideLoadMoreSpinner();
                        showData(resultList);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }
                });
    }

    private void showData(List<Result> results) {
        popularTvShowsAdapter.addData(results);
    }

    public void showLoadMoreSpinner() {
        showsRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                popularTvShowsAdapter.addLoadingFooter();
            }
        });
    }

    public void hideLoadMoreSpinner() {
        popularTvShowsAdapter.removeLoadingFooter();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        CursorLoader mCursorLoader;
        uri = ShowContract.PopularShows.uri;
        mCursorLoader = new CursorLoader(getActivity(), uri,
                ShowContract.PopularShows.projectionsForMainActivity,
                null, null, null);
        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            resultList = getShowsFromCursor(data);
            initAdapter(resultList, 1);
            progressBar.setVisibility(View.GONE);
            frameLayout.setBackgroundColor(Color.BLACK);
        }
    }

    private List<Result> getShowsFromCursor(Cursor data) {
        results = new ArrayList<>();
        if (data != null) {
            data.moveToFirst();
            while (data.moveToNext()) {
                int showId = data.getInt(data.getColumnIndex(ShowContract.PopularShows.COLUMN_ID));
                String title = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_TITLE));
                String poster = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_POSTER));
                String releaseDate = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_RELEASE_DATE));
                String rate = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_VOTE_AVERAGE));
                String overview = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_OVERVIEW));
                String backdropImage = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_BACKDROP_IMG));
                Log.d(TAG, title);
                Result popularResultData = new Result();
                popularResultData.setId(showId);
                popularResultData.setName(title);
                popularResultData.setPosterPath(poster);
                popularResultData.setFirstAirDate(releaseDate);
                popularResultData.setVoteAverage(Double.parseDouble(rate));
                popularResultData.setOverview(overview);
                popularResultData.setBackdropPath(backdropImage);
                results.add(popularResultData);
            }
            data.moveToFirst();
            while (data.moveToNext()) {
                int showId = data.getInt(data.getColumnIndex(ShowContract.PopularShows.COLUMN_ID));
                String title = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_TITLE));
                String poster = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_POSTER));
                String releaseDate = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_RELEASE_DATE));
                String rate = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_VOTE_AVERAGE));
                String overview = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_OVERVIEW));
                String backdropImage = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_BACKDROP_IMG));
                String trailer = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_TRAILER));
                PopularResultData popularResultData = new PopularResultData();
                popularResultData.setId(showId);
                popularResultData.setTitle(title);
                popularResultData.setPosterPath(poster);
                popularResultData.setReleaseDate(releaseDate);
                popularResultData.setRating(rate);
                popularResultData.setOverview(overview);
                popularResultData.setBackDropImagePath(backdropImage);
                popularResultData.setTrailer(trailer);
                Log.d(TAG, trailer + " TRAILERS FROM MAIN");
                popularResultList.add(popularResultData);
            }
        }
        return results;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        initAdapter(null, 1);
    }


    @Override
    public void onClick(Result result, int position) {
        //TODO:Implementation of master Detail
        gson = new Gson();
        if (position >= last) {
            //Send results
            popularResultData = new PopularResultData();
            popularResultData.setId(result.getId());
            popularResultData.setTitle(result.getName());
            popularResultData.setPosterPath(result.getPosterPath());
            popularResultData.setReleaseDate(result.getFirstAirDate());
            popularResultData.setRating(String.valueOf(result.getVoteAverage()));
            popularResultData.setOverview(result.getOverview());
            popularResultData.setBackDropImagePath(result.getBackdropPath());
            Log.d(TAG, "On page greater than 1");
            sharedPreferences.edit()
                    .putBoolean(getString(R.string.page_key_preference), true).apply();
            //fetch characters, similar,trailer
            //Don't Cache when Page greater than 1
            fetchDetailsForPageMoreThanOne(result.getId(), popularResultData);
        } else {
            //Send popularResults
            //which me be cached
            Log.d(TAG, "On page 1");
            sharedPreferences.edit()
                    .putBoolean(getString(R.string.page_key_preference), false).apply();
            PopularResultData popularResultDataClicked = null;
            for (int i = 0; i < popularResultList.size(); i++) {
                if (popularResultList.get(i).getTitle().equals(result.getName())) {
                    popularResultDataClicked = popularResultList.get(i);
                    break;
                }
            }
            Log.d(TAG, "Clicked Trailer" + popularResultDataClicked.getTrailer());
            //if popular ResultData clicked exist in DB show from there
            //otherwise hit API to getCrew and getSimilar Shows
            //And insert them in DB on completion
            String projections[] = {ShowContract.PopularShows._ID,
                    ShowContract.PopularShows.COLUMN_ID,
                    ShowContract.PopularShows.COLUMN_POSTER,
                    ShowContract.PopularShows.COLUMN_TITLE,
                    ShowContract.PopularShows.COLUMN_RELEASE_DATE,
                    ShowContract.PopularShows.COLUMN_VOTE_AVERAGE,
                    ShowContract.PopularShows.COLUMN_OVERVIEW,
                    ShowContract.PopularShows.COLUMN_TRAILER,
                    ShowContract.PopularShows.COLUMN_BACKDROP_IMG,
                    ShowContract.PopularShows.COLUMN_CHARACTERS,
                    ShowContract.PopularShows.COLUMN_SIMILAR_SHOWS};
            Cursor cursorForClickedId = getActivity().getContentResolver()
                    .query(ShowContract.PopularShows.uri, projections,
                            ShowContract.PopularShows.COLUMN_ID + " =?",
                            new String[]{String.valueOf(popularResultDataClicked.getId())},
                            null);
            if (cursorForClickedId != null) {
                cursorForClickedId.moveToFirst();
                String characters = cursorForClickedId.getString(cursorForClickedId
                        .getColumnIndex(ShowContract.PopularShows.COLUMN_CHARACTERS));
                String similarShows = cursorForClickedId.getString(cursorForClickedId
                        .getColumnIndex(ShowContract.PopularShows.COLUMN_SIMILAR_SHOWS));
                if (!(characters == null && similarShows == null)) {
                    //Record exist fetch from DB
                    Log.d(TAG, "Exist in DB");
                    //Fetching from DB

                } else {
                    Log.d(TAG, "Fetch for page 1");
                    fetchDetailsForPageOne(popularResultDataClicked.getId(),
                            popularResultDataClicked);
                }
                cursorForClickedId.close();
            } else {
                Log.d(TAG, "Cursor Null");
            }
        }
    }

    private void fetchDetailsForPageMoreThanOne(Integer id, PopularResultData popularResultData) {
        videoResultObservable = retrofit
                .create(ApiInterface.class).getTrailers(id, BuildConfig.TV_KEY);
        videoResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(videoResult -> {
                    videoDetailResults = videoResult.getResults();
                    if (videoDetailResults != null) {
                        if (videoDetailResults.size() > 0) {
                            for (VideoDetailResult videoDetailResult : videoDetailResults) {
                                Log.d(TAG, videoDetailResult.getKey() + " KEY");
                                if (videoDetailResult.getKey().length() != 0) {
                                    Uri trailerUri = Uri.parse(Constants.YOUTUBE_BASE_URL)
                                            .buildUpon()
                                            .appendQueryParameter("v",
                                                    videoDetailResult.getKey())
                                            .build();
                                    trailer = trailerUri.toString();
                                    break;
                                } else {
                                    //handle when not returned
                                    trailer = getActivity()
                                            .getString(R.string.trailer_not_available_error);
                                }
                            }

                        } else {
                            //handle null
                            trailer = getActivity()
                                    .getString(R.string.trailer_not_available_error);
                        }
                        popularResultData.setTrailer(trailer);
                    }
                    return retrofit.create(ApiInterface.class).getCrew(id, BuildConfig.TV_KEY)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }).concatMap(crewResult -> {
            castList = crewResult.getCast();
            String castListJson = gson.toJson(castList);
            popularResultData.setCharacters(castListJson);
            return retrofit.create(ApiInterface.class).getSimilarShows(id, BuildConfig.TV_KEY)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }).subscribe(new Observer<SimilarShowResults>() {

            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(SimilarShowResults similarShowResults) {
                similarShowDetails = similarShowResults.getResults();
                String similarShowsJson = gson.toJson(similarShowDetails);
                popularResultData.setSimilarShows(similarShowsJson);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onComplete() {
                if (!(disposable.isDisposed())) {
                    disposable.dispose();
                }
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                String detailsJson = gson.toJson(popularResultData);
                intent.putExtra(Constants.KEY_POPULAR_SHOWS, detailsJson);
                startActivity(intent);
            }
        });
    }

    private void fetchDetailsForPageOne(int id, PopularResultData popularResultData) {
        Log.d(TAG, "Fetching for 1");
        Observable<CharacterResult> characterResultObservable =
                retrofit.create(ApiInterface.class).getCrew(id, BuildConfig.TV_KEY);
        characterResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(crewResults -> {
                    castDetailResult = crewResults.getCast();
                    Log.d(TAG, " Char for Page 1 " + castDetailResult.get(0).getCharacter());
                    characterJson = gson.toJson(castDetailResult);
                    popularResultData.setCharacters(characterJson);
                    return retrofit.create(ApiInterface.class)
                            .getSimilarShows(id, BuildConfig.TV_KEY)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }).subscribe(new Observer<SimilarShowResults>() {

            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(SimilarShowResults similarShowResults) {
                similarShowDetailInfo = similarShowResults.getResults();
                similarShowsResult = gson.toJson(similarShowDetailInfo);
                Log.d(TAG, "Similar for Page 1" + similarShowsResult);
                popularResultData.setSimilarShows(similarShowsResult);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                popularShowsJson = gson.toJson(popularResultData);
                intent.putExtra(Constants.KEY_POPULAR_SHOWS, popularShowsJson);
                Log.d(TAG, popularShowsJson);
                insertCrewAndSimilarShows(similarShowsResult, characterJson, popularResultData);
                startActivity(intent);
            }
        });
    }

    private void insertCrewAndSimilarShows(String similarShowsResult,
                                           String characterJson,
                                           PopularResultData popularResultData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShowContract.PopularShows.COLUMN_ID, popularResultData.getId());
        contentValues.put(ShowContract.PopularShows.COLUMN_TITLE, popularResultData.getTitle());
        contentValues.put(ShowContract.PopularShows.COLUMN_POSTER
                , popularResultData.getPosterPath());
        contentValues.put(ShowContract.PopularShows.COLUMN_RELEASE_DATE,
                popularResultData.getReleaseDate());
        contentValues.put(ShowContract.PopularShows.COLUMN_VOTE_AVERAGE,
                popularResultData.getRating());
        contentValues.put(ShowContract.PopularShows.COLUMN_OVERVIEW, popularResultData.getOverview());
        contentValues.put(ShowContract.PopularShows.COLUMN_TRAILER, popularResultData.getTrailer());
        contentValues.put(ShowContract.PopularShows.COLUMN_BACKDROP_IMG,
                popularResultData.getBackDropImagePath());
        contentValues.put(ShowContract.PopularShows.COLUMN_SIMILAR_SHOWS,
                similarShowsResult);
        contentValues.put(ShowContract.PopularShows.COLUMN_CHARACTERS, characterJson);
        getActivity().getContentResolver().update(ShowContract.PopularShows.uri,
                contentValues, ShowContract.PopularShows.COLUMN_ID + " =?",
                new String[]{String.valueOf(popularResultData.getId())});
        Log.d(TAG, "Loaded DATA");
    }
}
