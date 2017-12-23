package com.developers.telelove.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.developers.telelove.App;
import com.developers.telelove.BuildConfig;
import com.developers.telelove.R;
import com.developers.telelove.adapters.PopularTvShowsAdapter;
import com.developers.telelove.model.PopularShowsModel.PopularPageResult;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.util.ApiInterface;
import com.developers.telelove.util.PaginationScrollListener;

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

public class MainActivity extends AppCompatActivity {

    public static final int START_PAGE = 1;
    @BindView(R.id.shows_recycler_view)
    RecyclerView showsRecyclerView;
    @Inject
    Retrofit retrofit;
    Observable<PopularPageResult> pageResultObservable;
    private PopularTvShowsAdapter popularTvShowsAdapter;
    private List<Result> resultList;
    private PaginationScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((App) getApplication()).getNetComponent().inject(this);
        pageResultObservable = retrofit.create(ApiInterface.class)
                .getPopularShows(BuildConfig.TV_KEY, START_PAGE);
        getPopularShowsFromApi(START_PAGE);
        popularTvShowsAdapter = new PopularTvShowsAdapter(getApplicationContext(), resultList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        showsRecyclerView.setLayoutManager(gridLayoutManager);
        showsRecyclerView.setAdapter(popularTvShowsAdapter);
        scrollListener = new PaginationScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getPopularShowsFromApi(current_page);
            }
        };
        showsRecyclerView.addOnScrollListener(scrollListener);
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


}
