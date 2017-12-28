package com.developers.telelove.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.developers.telelove.R;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedDetailResults;
import com.developers.telelove.util.Constants;
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
    private Context context;
    private List<TopRatedDetailResults> ratedDetailResults;
    private boolean isLoadingItemAdded = false;

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
                loadImage(backdrop, holder);
                ((TopRecyclerViewHolder) holder).topRatedTitle.setText(ratedDetailResults
                        .get(position).getName());
        }

    }

    private void loadImage(Uri backdrop, RecyclerView.ViewHolder holder) {
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
