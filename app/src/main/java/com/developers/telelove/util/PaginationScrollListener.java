package com.developers.telelove.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Amanjeet Singh on 23/12/17.
 */

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = PaginationScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 0; // The minimum amount of items to have below your current scroll position before loading more.
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private GridLayoutManager gridLayoutManager;

    public PaginationScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal + 1) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}
