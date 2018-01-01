package com.developers.telelove.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.developers.telelove.model.FavouriteShowsResult;
import com.developers.telelove.util.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amanjeet Singh on 29/12/17.
 */

public class FavouriteShowsAdapter extends RecyclerView.Adapter
        <FavouriteShowsAdapter.FavouriteShowsViewHolder> {


    private Context context;
    private List<FavouriteShowsResult> favouriteShowsResultList;
    private Utility.ClickCallBacks clickCallBacks;

    public FavouriteShowsAdapter(Context context, List<FavouriteShowsResult> favouriteShowsResults) {
        this.context = context;
        this.favouriteShowsResultList = favouriteShowsResults;
    }

    @Override
    public FavouriteShowsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourite_row,
                parent, false);
        return new FavouriteShowsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteShowsViewHolder holder, int position) {
        Picasso.with(context).load(favouriteShowsResultList.get(position).getBackDropImagePath())
                .into(holder.favouriteImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.favouriteProgressBar.setVisibility(View.GONE);
                        holder.favouriteCardView.setBackgroundColor(0xFF333333);
                    }

                    @Override
                    public void onError() {

                    }
                });
        holder.favouriteTitleText.setText(favouriteShowsResultList.get(position).getTitle());
        holder.favouriteCardView.setOnClickListener(view -> {
            clickCallBacks.onFavouriteShowClick(favouriteShowsResultList.get(position),
                    position);
        });
    }

    public void swap(List<FavouriteShowsResult> favouriteShowsResults) {
        this.favouriteShowsResultList.clear();
        this.favouriteShowsResultList.addAll(favouriteShowsResults);
        notifyDataSetChanged();
    }

    public void setClickCallBacks(Utility.ClickCallBacks clickCallBacks) {
        this.clickCallBacks = clickCallBacks;
    }

    @Override
    public int getItemCount() {
        return favouriteShowsResultList.size();
    }

    public class FavouriteShowsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.favourite_recyclerview_element)
        CardView favouriteCardView;
        @BindView(R.id.favourite_shows_progress)
        ProgressBar favouriteProgressBar;
        @BindView(R.id.textview_favourite_element)
        TextView favouriteTitleText;
        @BindView(R.id.imageview_favourite_element)
        ImageView favouriteImage;

        public FavouriteShowsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
