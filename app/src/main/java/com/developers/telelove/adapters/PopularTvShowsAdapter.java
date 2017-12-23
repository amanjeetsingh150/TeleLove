package com.developers.telelove.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.util.Constants;
import com.squareup.picasso.Callback;
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
    private Context context;
    private List<Result> resultList;
    private boolean isLoadingItemAdded = false;

    public PopularTvShowsAdapter(Context context, List<Result> resultList) {
        this.context = context;
        this.resultList = new ArrayList<>();
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
                Uri uri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                        .appendEncodedPath(resultList.get(position).getBackdropPath()).build();
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

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                ((PopularTvViewHolder) holder).popularShowTitle.setText(resultList.get(position).getName());
                break;
            case LOADING:
                //nothing
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

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
