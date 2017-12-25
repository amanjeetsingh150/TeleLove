package com.developers.telelove.adapters;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.developers.telelove.App;
import com.developers.telelove.R;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.util.Constants;
import com.developers.telelove.util.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amanjeet Singh on 23/12/17.
 */

public class PopularTvShowsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM = 0;
    public static final int LOADING = 1;
    private static final String TAG = PopularTvShowsAdapter.class.getSimpleName();
    private Context context;
    private List<Result> resultList;
    private int page;
    private String uri;
    private Uri backDropUri;
    private boolean isLoadingItemAdded = false;

    public PopularTvShowsAdapter(Context context, List<Result> resultList, int page) {
        this.context = context;
        this.resultList = resultList;
        this.page = page;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View v = inflater.inflate(R.layout.popular_list_row, parent, false);
                viewHolder = new PopularTvViewHolder(v);
                break;
            case LOADING:
                View view = inflater.inflate(R.layout.item_load, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                uri = resultList.get(position).getBackdropPath();
                if (Utility.validateUriForAppending(uri)) {
                    //Required to build Uri
                    backDropUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                            .appendEncodedPath(uri).build();
                    //load without cache because we do not want large number of image to be in cache
                    loadWithoutCache(backDropUri, holder);
                } else {
                    //load with cache
                    loadWithCache(resultList.get(position).getBackdropPath(), holder);
                }
                ((PopularTvViewHolder) holder).popularShowTitle
                        .setText(resultList.get(position).getName());
                break;
            case LOADING:
                if (!Utility.isNetworkConnected(context)) {
                    ((LoadingViewHolder) holder).syncFb.setVisibility(View.VISIBLE);
                    ((LoadingViewHolder) holder).progressBarForMoreShows.setVisibility(View.GONE);
                }

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == resultList.size() - 1 && isLoadingItemAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingItemAdded = true;
        Result result = new Result();
        addItem(result);
    }

    public void addItem(Result resultItem) {
        resultList.add(resultItem);
        notifyItemInserted(resultList.size() - 1);
    }

    public void addData(List<Result> results) {
        this.resultList.addAll(results);
        notifyDataSetChanged();
    }

    public void removeLoadingFooter() {
        if (isLoadingItemAdded) {
            isLoadingItemAdded = false;

            int position = resultList.size() - 1;
            Result resultItem = resultList.get(position);

            if (resultItem != null) {
                resultList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    private void loadWithoutCache(Uri uri, final RecyclerView.ViewHolder holder) {
        Picasso.with(context).load(uri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette palette = Palette.from(bitmap).generate();
                ((PopularTvViewHolder) holder).popularShowImage.setImageBitmap(bitmap);
                ((PopularTvViewHolder) holder).progressBar.setVisibility(View.GONE);
                int color = palette.getMutedColor(0xFF333333);
                ((PopularTvViewHolder) holder).popularCardViewElement.setBackgroundColor(color);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "Failed to load");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void loadWithCache(String imageUrl, final RecyclerView.ViewHolder holder) {
        Log.d(TAG, " With cache " + imageUrl);
        App.picassoWithCache.with(context).load(imageUrl).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette palette = Palette.from(bitmap).generate();
                ((PopularTvViewHolder) holder).popularShowImage.setImageBitmap(bitmap);
                ((PopularTvViewHolder) holder).progressBar.setVisibility(View.GONE);
                int color = palette.getMutedColor(0xFF333333);
                ((PopularTvViewHolder) holder).popularCardViewElement.setBackgroundColor(color);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "Failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class PopularTvViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recyclerview_element)
        CardView popularCardViewElement;

        @BindView(R.id.imageview_popular_element)
        ImageView popularShowImage;

        @BindView(R.id.textview_popular_element)
        TextView popularShowTitle;

        @BindView(R.id.popular_shows_progress)
        ProgressBar progressBar;

        public PopularTvViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.paged_progress_bar)
        ProgressBar progressBarForMoreShows;
        @BindView(R.id.sync_fb)
        FloatingActionButton syncFb;

        LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
