package com.developers.telelove.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.developers.telelove.model.SimilarShowsResult.SimilarShowDetails;
import com.developers.telelove.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amanjeet Singh on 28/12/17.
 */

public class SimilarShowsAdapter extends RecyclerView.Adapter<SimilarShowsAdapter.SimilarShowsViewHolder> {

    private Context context;
    private List<SimilarShowDetails> similarShowDetailsList;
    private Uri imageUri;


    public SimilarShowsAdapter(Context context, List<SimilarShowDetails> details) {
        this.context = context;
        this.similarShowDetailsList = details;
    }

    @Override
    public SimilarShowsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.similar_shows_list,
                parent, false);
        return new SimilarShowsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimilarShowsViewHolder holder, int position) {
        imageUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                .appendEncodedPath(similarShowDetailsList.get(position).getPosterPath()).build();
        Log.d("SIMILAR ", " " + imageUri);
        Picasso.with(context).load(imageUri).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.similarImage.setImageBitmap(bitmap);
                Palette palette = Palette.from(bitmap).generate();
                int color = palette.getMutedColor(0xFF333333);
                holder.similarCardView.setBackgroundColor(color);
                holder.similarProgress.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        holder.similarShowTitle.setText(similarShowDetailsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return similarShowDetailsList.size();
    }

    public class SimilarShowsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.similar_title_text_view)
        TextView similarShowTitle;
        @BindView(R.id.similar_image_view)
        ImageView similarImage;
        @BindView(R.id.similar_card_view)
        CardView similarCardView;
        @BindView(R.id.similar_progress_bar)
        ProgressBar similarProgress;

        public SimilarShowsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
