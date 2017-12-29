package com.developers.telelove.ui;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.R;
import com.developers.telelove.adapters.PopularShowsAdapter;
import com.developers.telelove.adapters.TopRatedShowsAdapter;
import com.developers.telelove.data.ShowContract;
import com.developers.telelove.model.CharactersModel.Cast;
import com.developers.telelove.model.CharactersModel.CharacterResult;
import com.developers.telelove.model.PopularShowsModel.PopularPageResult;
import com.developers.telelove.model.PopularShowsModel.PopularResultData;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowDetails;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowResults;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedDetailResults;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedResults;
import com.developers.telelove.model.VideosModel.VideoDetailResult;
import com.developers.telelove.model.VideosModel.VideoResult;
import com.developers.telelove.ui.activities.DetailActivity;
import com.developers.telelove.ui.activities.SettingsActivity;
import com.developers.telelove.util.ApiInterface;
import com.developers.telelove.util.Constants;
import com.developers.telelove.util.PaginationScrollListener;
import com.developers.telelove.util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    public static boolean changed = false;
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
    String resultJson, preference;
    Gson gson;
    int last;
    @BindView(R.id.frame_layout_list)
    FrameLayout frameLayout;
    Observable<TopRatedResults> ratedResultsObservable;
    Uri uri;
    CursorLoader mCursorLoader;
    TopRatedShowsAdapter topRatedShowsAdapter;
    private PopularShowsAdapter popularShowsAdapter;
    private List<Result> resultList = new ArrayList<>();
    private List<TopRatedDetailResults> ratedDetailResults;
    private List<PopularResultData> popularResultList = new ArrayList<>();

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
        preference = sharedPreferences.getString(getActivity().getString
                (R.string.preferences_key), "0");
        setHasOptionsMenu(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        switch (preference) {
            case "0":
                getPopularShowsFromApi(1);
                frameLayout.setBackgroundColor(Color.BLACK);
                break;
            case "1":
                getTopRatedShowsFromApi(START_PAGE);
                frameLayout.setBackgroundColor(Color.BLACK);
                break;
        }
        showsRecyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                preference = sharedPreferences.getString(getActivity().getString
                        (R.string.preferences_key), "0");
                Log.d(TAG,"preference "+current_page +preference);
                if (Utility.isNetworkConnected(getActivity())) {
                    switch (preference) {
                        case "0":
                            getPopularShowsFromApi(current_page);
                            break;
                        case "1":
                            getTopRatedShowsFromApi(current_page);
                            break;
                    }
                } else {
                    Log.d(TAG, "No internet");
                    showLoadMoreSpinner();
                }
            }
        };
        showsRecyclerView.addOnScrollListener(scrollListener);
        //getActivity().getSupportLoaderManager().initLoader(SHOWS_LOADER, null, this);
        return view;
    }

    private void initAdapter(List<Result> results) {
        if (results != null) {
            popularShowsAdapter = new PopularShowsAdapter(getActivity(), results);
            popularShowsAdapter.setClickCallBacks(this);
            showsRecyclerView.setAdapter(popularShowsAdapter);
            last = linearLayoutManager.getItemCount();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void initTopRatedAdapter(List<TopRatedDetailResults> ratedResults) {
        topRatedShowsAdapter = new TopRatedShowsAdapter(getActivity(), ratedResults);
        topRatedShowsAdapter.setClickCallBacks(this);
        showsRecyclerView.setAdapter(topRatedShowsAdapter);
    }

    public void getPopularShowsFromApi(int page) {
        Log.d(TAG, "Getting Popular");
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
                            if (!(page > 1)) {
                                initAdapter(resultList);
                            }
                        }
                    }
                });
    }

    public void getTopRatedShowsFromApi(int page) {

        if (page > 1) {
            showLoadMoreSpinner();
        }

        ratedResultsObservable = retrofit.create(ApiInterface.class)
                .getTopRatedShows(BuildConfig.TV_KEY, page);
        ratedResultsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TopRatedResults>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(TopRatedResults topRatedResults) {
                        ratedDetailResults = topRatedResults.getResults();
                        hideLoadMoreForTopRated();
                        showDataForTopRated(ratedDetailResults);
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
                        if (!(page > 1)) {
                            initTopRatedAdapter(ratedDetailResults);
                        }
                    }
                });
    }

    private void showData(List<Result> results) {
        if (popularShowsAdapter != null) {
            popularShowsAdapter.addData(results);
        }
    }

    private void showDataForTopRated(List<TopRatedDetailResults> results) {
        if (topRatedShowsAdapter != null) {
            topRatedShowsAdapter.addData(results);
        }
    }


    public void showLoadMoreSpinner() {
        showsRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                preference = sharedPreferences.getString(getActivity().getString
                        (R.string.preferences_key), "0");
                switch (preference){
                    case "0":
                        popularShowsAdapter.addLoadingFooter();
                        break;
                    case "1":
                        topRatedShowsAdapter.addLoadingFooter();
                        break;
                }
            }
        });
    }

    public void hideLoadMoreSpinner() {
        if (popularShowsAdapter != null) {
            popularShowsAdapter.removeLoadingFooter();
        }
    }

    public void hideLoadMoreForTopRated() {
        if (topRatedShowsAdapter != null) {
            topRatedShowsAdapter.removeLoadingFooter();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        preference = sharedPreferences.getString(getActivity()
                .getString(R.string.preferences_key), "0");
        switch (preference) {
            case "0":
                uri = ShowContract.PopularShows.uri;
                Log.d(TAG, "URI " + uri);
                mCursorLoader = new CursorLoader(getActivity(), uri,
                        ShowContract.PopularShows.projectionsForMainActivity,
                        null, null, null);
                break;
        }
        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        preference = sharedPreferences.getString(getActivity().getString(
                R.string.preferences_key), "0");
        switch (preference) {
            case "0":
                if (data.getCount() > 0) {
                    resultList = getShowsFromCursor(data);
                    initAdapter(resultList);
                    progressBar.setVisibility(View.GONE);
                    frameLayout.setBackgroundColor(Color.BLACK);
                }
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Utility.isNetworkConnected(getActivity())) {
            if (changed) {
                preference = sharedPreferences.getString(getActivity()
                        .getString(R.string.preferences_key), "0");
                switch (preference) {
                    case "0":
                        getPopularShowsFromApi(START_PAGE);
                        break;
                    case "1":
                        getTopRatedShowsFromApi(START_PAGE);
                        break;
                }
            }

        } else {

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
        initAdapter(null);
    }


    @Override
    public void onClick(Result result, int position) {
        //TODO:Implementation of master Detail
        gson = new Gson();
        resultJson = gson.toJson(result);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Constants.KEY_POPULAR_SHOWS, resultJson);
        startActivity(intent);
    }

    @Override
    public void onRatedShowClick(TopRatedDetailResults ratedDetailResults, int position) {
        gson = new Gson();
        String topRatedShowJson = gson.toJson(ratedDetailResults);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Constants.KEY_TOP_RATED, topRatedShowJson);
        startActivity(intent);
    }

}
