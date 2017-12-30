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
import com.developers.telelove.model.CharactersModel.Cast;
import com.developers.telelove.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amanjeet Singh on 28/12/17.
 */

public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListAdapter
        .CharacterViewHolder> {

    private Context context;
    private List<Cast> castList;

    public CharacterListAdapter(Context context, List<Cast> catlList) {
        this.context = context;
        this.castList = catlList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.character_list,
                parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, int position) {
        Uri charImageUri = Uri.parse(Constants.BASE_URL_IMAGES)
                .buildUpon().appendEncodedPath(castList.get(position).getProfilePath()).build();
        Log.d("IMAGE ", charImageUri.toString() + " 0------"+castList.get(position).getCharacter());
        Picasso.with(context).load(charImageUri).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.castImage.setImageBitmap(bitmap);
                Palette palette = Palette.from(bitmap).generate();
                int color = palette.getMutedColor(0xFF333333);
                holder.cardView.setBackgroundColor(color);
                holder.characterProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("errr", "ERRORRRRRR "+charImageUri);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        holder.nameTextView.setText(castList.get(position).getCharacter());
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.character_image_view)
        ImageView castImage;
        @BindView(R.id.char_name_text_view)
        TextView nameTextView;
        @BindView(R.id.character_progress_bar)
        ProgressBar characterProgressBar;
        @BindView(R.id.card_view)
        CardView cardView;

        public CharacterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
