package com.developers.telelove.ui;


import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.developers.telelove.adapters.CharacterListAdapter;
import com.developers.telelove.adapters.SimilarShowsAdapter;
import com.developers.telelove.model.CharactersModel.Cast;
import com.developers.telelove.model.PopularShowsModel.PopularResultData;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowDetails;
import com.developers.telelove.util.Constants;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    String detailsJson;
    @Inject
    SharedPreferences sharedPreferences;
    @BindView(R.id.poster_image_view)
    ImageView posterImageView;
    @BindView(R.id.progress_poster)
    ProgressBar progressBar;
    @BindView(R.id.title_text_view)
    TextView titleTextView;
    @BindView(R.id.rating_text_view)
    TextView ratingText;
    @BindView(R.id.favorite_material_button)
    MaterialFavoriteButton materialFavoriteButton;
    @BindView(R.id.overview_text_view)
    TextView overviewTextView;
    @BindView(R.id.app_bar_img)
    ImageView backDropImage;
    @BindView(R.id.character_recycler_view)
    RecyclerView characterRecyclerView;
    Gson gson;
    boolean pageGreaterThanOne;
    SimilarShowsAdapter similarShowsAdapter;
    @BindView(R.id.similar_shows_recycler_view)
    RecyclerView similarShowsRecyclerView;
    LinearLayoutManager characterLayoutManager, similarShowLayoutManager;
    private PopularResultData popularResultData;
    private CharacterListAdapter characterListAdapter;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        detailsJson = bundle.getString(Constants.KEY_DETAILS);
        gson = new Gson();
        popularResultData = gson.fromJson(detailsJson, PopularResultData.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        ((App) getActivity().getApplication()).getNetComponent().inject(this);
        pageGreaterThanOne = sharedPreferences
                .getBoolean(getActivity().getString(R.string.page_key_preference), false);
        if (pageGreaterThanOne) {
            Uri posterUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                    .appendEncodedPath(popularResultData.getPosterPath()).build();
            Picasso.with(getActivity()).load(posterUri.toString())
                    .into(posterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            Uri backDropUri = Uri.parse(Constants.BASE_URL_IMAGES)
                    .buildUpon().appendEncodedPath(popularResultData.getBackDropImagePath())
                    .build();
            Picasso.with(getActivity()).load(backDropUri.toString())
                    .into(backDropImage);
        } else {

            Picasso.with(getActivity()).load(popularResultData.getPosterPath())
                    .into(posterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
            Picasso.with(getActivity()).load(popularResultData.getBackDropImagePath())
                    .into(backDropImage);
        }
        titleTextView.setText(popularResultData.getTitle());
        overviewTextView.setText(popularResultData.getOverview());
        ratingText.setText(popularResultData.getRating());
        materialFavoriteButton.setOnClickListener((v) -> {
            Snackbar.make(v, "Added to favorites", Snackbar.LENGTH_SHORT).show();
            materialFavoriteButton.setAnimateFavorite(true);
        });
        String charactersJson = popularResultData.getCharacters();
        List<Cast> castList = gson.fromJson(charactersJson,
                new TypeToken<List<Cast>>() {
                }.getType());
        characterListAdapter = new CharacterListAdapter(getActivity(), castList);
        characterLayoutManager = new LinearLayoutManager(getActivity());
        characterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        characterRecyclerView.setLayoutManager(characterLayoutManager);
        characterRecyclerView.setAdapter(characterListAdapter);
        String similarShowJson = popularResultData.getSimilarShows();
        List<SimilarShowDetails> similarShowDetails = gson.fromJson(similarShowJson,
                new TypeToken<List<SimilarShowDetails>>() {
                }.getType());
        similarShowsAdapter = new SimilarShowsAdapter(getActivity(), similarShowDetails);
        similarShowLayoutManager = new LinearLayoutManager(getActivity());
        similarShowLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        similarShowsRecyclerView.setLayoutManager(similarShowLayoutManager);
        similarShowsRecyclerView.setAdapter(similarShowsAdapter);
        return view;
    }

}
