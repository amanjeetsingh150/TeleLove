package com.developers.telelove.ui;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.R;
import com.developers.telelove.adapters.PopularTvShowsAdapter;
import com.developers.telelove.data.ShowContract;
import com.developers.telelove.model.PopularShowsModel.PopularPageResult;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.util.ApiInterface;
import com.developers.telelove.util.PaginationScrollListener;

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

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int START_PAGE = 1;
    private static final int SHOWS_LOADER = 2;
    @BindView(R.id.shows_recycler_view)
    RecyclerView showsRecyclerView;
    @BindView(R.id.progress_bar_grid)
    ProgressBar progressBar;
    @Inject
    Retrofit retrofit;
    Observable<PopularPageResult> pageResultObservable;
    private PopularTvShowsAdapter popularTvShowsAdapter;
    private List<Result> resultList = new ArrayList<>();
    private PaginationScrollListener scrollListener;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((App) getApplication()).getNetComponent().inject(this);
        initAdapter(resultList);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        showsRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (popularTvShowsAdapter.getItemViewType(position) ==
                        PopularTvShowsAdapter.LOADING) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        scrollListener = new PaginationScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getPopularShowsFromApi(current_page);
            }
        };
        showsRecyclerView.addOnScrollListener(scrollListener);
        getSupportLoaderManager().initLoader(SHOWS_LOADER, null, this);
    }

    private void initAdapter(List<Result> results) {
        popularTvShowsAdapter = new PopularTvShowsAdapter(getApplicationContext(),
                results);
        showsRecyclerView.setAdapter(popularTvShowsAdapter);
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
        mCursorLoader = new CursorLoader(getApplicationContext(), uri,
                ShowContract.PopularShows.projectionsForMainActivity,
                null, null, null);
        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            getShowsFromCursor(data);
        }
    }

    private void getShowsFromCursor(Cursor data) {
        List<Result> results = new ArrayList<>();
        if (data != null) {
            data.moveToFirst();
            while (data.moveToNext()) {
                int showId = data.getInt(data.getColumnIndex(ShowContract.PopularShows.COLUMN_ID));
                String title = data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_TITLE));
                String poster=data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_POSTER));
                String releaseDate=data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_RELEASE_DATE));
                String rate=data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_VOTE_AVERAGE));
                String overview=data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_OVERVIEW));
                String trailer=data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_TRAILER));
                String backdropImage=data.getString(data.getColumnIndex(ShowContract.PopularShows.COLUMN_BACKDROP_IMG));

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        initAdapter(null);
    }
}
