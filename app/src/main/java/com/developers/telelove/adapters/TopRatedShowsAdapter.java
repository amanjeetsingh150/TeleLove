package com.developers.telelove.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.developers.telelove.R;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedDetailResults;
import com.developers.telelove.util.Constants;
import com.developers.telelove.util.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amanjeet Singh on 28/12/17.
 */

public class TopRatedShowsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String TAG = TopRatedShowsAdapter.class.getSimpleName();
    private Context context;
    private List<TopRatedDetailResults> ratedDetailResults;
    private boolean isLoadingItemAdded = false;
    private Utility.ClickCallBacks clickCallBacks;

    public TopRatedShowsAdapter(Context context
            , List<TopRatedDetailResults> ratedDetailResults) {
        this.context = context;
        this.ratedDetailResults = ratedDetailResults;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View v = inflater.inflate(R.layout.top_rated_row, parent, false);
                viewHolder = new TopRecyclerViewHolder(v);
                break;
            case LOADING:
                View view = inflater.inflate(R.layout.item_load, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case ITEM:
                Uri backdrop = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                        .appendEncodedPath(ratedDetailResults.get(position).getBackdropPath()).build();
                loadImage(backdrop, holder, position);
                ((TopRecyclerViewHolder) holder).topRatedTitle.setText(ratedDetailResults
                        .get(position).getName());
                ((TopRecyclerViewHolder) holder).topRatedImage.setOnClickListener(v -> {
                    clickCallBacks.onRatedShowClick(ratedDetailResults.get(position),
                            position);
                });

                break;
            case LOADING:
                if (!Utility.isNetworkConnected(context)) {
                    ((LoadingViewHolder) holder).syncFb.setVisibility(View.VISIBLE);
                    ((LoadingViewHolder) holder).progressBarForMoreShows
                            .setVisibility(View.GONE);
                    ((LoadingViewHolder) holder).syncFb.setOnClickListener(v -> {
                        if (Utility.isNetworkConnected(context)) {
                            //Load the next page
                            Log.d(TAG, "Broadcast for next page loading");
                        } else {
                            Snackbar.make(v, context.getString(R.string.no_internet_connection),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }

    public void setClickCallBacks(Utility.ClickCallBacks clickCallBacks) {
        this.clickCallBacks = clickCallBacks;
    }

    private void loadImage(Uri backdrop, RecyclerView.ViewHolder holder, int position) {
        Picasso.with(context).load(backdrop).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette palette = Palette.from(bitmap).generate();
                ((TopRecyclerViewHolder) holder).topRatedImage.setImageBitmap(bitmap);
                ((TopRecyclerViewHolder) holder).topRatedProgressBar.setVisibility(View.GONE);
                int color = palette.getMutedColor(0xFF333333);
                ((TopRecyclerViewHolder) holder).cardView.setBackgroundColor(color);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                ((TopRecyclerViewHolder) holder).topRatedImage
                        .setBackgroundResource(R.drawable.user_placeholder);
                if (ratedDetailResults.get(position).getName() != null) {
                    ((TopRecyclerViewHolder) holder).topRatedTitle
                            .setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return (position == ratedDetailResults.size() - 1 && isLoadingItemAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return ratedDetailResults.size();
    }

    public void addLoadingFooter() {
        isLoadingItemAdded = true;
        TopRatedDetailResults result = new TopRatedDetailResults();
        addItem(result);
    }

    public void addItem(TopRatedDetailResults resultItem) {
        ratedDetailResults.add(resultItem);
        notifyItemInserted(ratedDetailResults.size() - 1);
    }

    public void addData(List<TopRatedDetailResults> results) {
        this.ratedDetailResults.addAll(results);
        notifyDataSetChanged();
    }

    public void removeLoadingFooter() {
        if (isLoadingItemAdded) {
            isLoadingItemAdded = false;

            int position = ratedDetailResults.size() - 1;
            TopRatedDetailResults resultItem = ratedDetailResults.get(position);

            if (resultItem != null) {
                ratedDetailResults.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public class TopRecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.top_rated_recyclerview_element)
        CardView cardView;
        @BindView(R.id.topRated_shows_progress)
        ProgressBar topRatedProgressBar;
        @BindView(R.id.imageview_topRated_element)
        ImageView topRatedImage;
        @BindView(R.id.textview_topRated_element)
        TextView topRatedTitle;

        public TopRecyclerViewHolder(View itemView) {
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
