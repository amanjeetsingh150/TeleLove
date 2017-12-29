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
import com.developers.telelove.BuildConfig;
import com.developers.telelove.R;
import com.developers.telelove.adapters.CharacterListAdapter;
import com.developers.telelove.adapters.SimilarShowsAdapter;
import com.developers.telelove.model.CharactersModel.Cast;
import com.developers.telelove.model.CharactersModel.CharacterResult;
import com.developers.telelove.model.PopularShowsModel.PopularResultData;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowDetails;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowResults;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedDetailResults;
import com.developers.telelove.model.VideosModel.VideoResult;
import com.developers.telelove.util.ApiInterface;
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
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private static final String TAG = DetailsFragment.class.getSimpleName();
    String detailsJson, preference;
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
    @Inject
    Retrofit retrofit;
    boolean pageGreaterThanOne;
    SimilarShowsAdapter similarShowsAdapter;
    @BindView(R.id.similar_shows_recycler_view)
    RecyclerView similarShowsRecyclerView;
    LinearLayoutManager characterLayoutManager, similarShowLayoutManager;
    Observable<VideoResult> videoResultObservable;
    Observable<CharacterResult> characterResultObservable;
    Observable<SimilarShowResults> similarShowResultsObservable;
    List<SimilarShowDetails> similarShowDetails;
    private Result popularResultData;
    private TopRatedDetailResults ratedDetailResults;
    private CharacterListAdapter characterListAdapter;
    private List<Cast> castList;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getNetComponent().inject(this);
        Bundle bundle = getArguments();
        detailsJson = bundle.getString(Constants.KEY_DETAILS);
        gson = new Gson();
        preference = sharedPreferences.getString(getActivity().getString(R.string.preferences_key),
                "0");
        Log.d(TAG, " " + preference + detailsJson);
        switch (preference) {
            case "0":
                popularResultData = gson.fromJson(detailsJson, Result.class);
                break;
            case "1":
                ratedDetailResults = gson.fromJson(detailsJson, TopRatedDetailResults.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        switch (preference) {
            case "0":
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
                        .buildUpon().appendEncodedPath(popularResultData.getBackdropPath())
                        .build();
                Picasso.with(getActivity()).load(backDropUri.toString())
                        .into(backDropImage);
                fetchCrewAndSimilarShowDetails(popularResultData.getId());
                titleTextView.setText(popularResultData.getName());
                overviewTextView.setText(popularResultData.getOverview());
                ratingText.setText(String.valueOf(popularResultData.getVoteAverage()));
                materialFavoriteButton.setOnClickListener((v) -> {
                    Snackbar.make(v, "Added to favorites", Snackbar.LENGTH_SHORT).show();
                    materialFavoriteButton.setAnimateFavorite(true);
                });
                break;
            case "1":
                Uri topRatedPosterUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                        .appendEncodedPath(ratedDetailResults.getPosterPath()).build();
                Picasso.with(getActivity()).load(topRatedPosterUri.toString())
                        .into(posterImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                Uri topRatedBackDropUri = Uri.parse(Constants.BASE_URL_IMAGES)
                        .buildUpon().appendEncodedPath(ratedDetailResults.getBackdropPath())
                        .build();
                Picasso.with(getActivity()).load(topRatedBackDropUri.toString())
                        .into(backDropImage);
                fetchCrewAndSimilarShowDetails(ratedDetailResults.getId());
                titleTextView.setText(ratedDetailResults.getName());
                overviewTextView.setText(ratedDetailResults.getOverview());
                ratingText.setText(String.valueOf(ratedDetailResults.getVoteAverage()));
                materialFavoriteButton.setOnClickListener((v) -> {
                    Snackbar.make(v, "Added to favorites", Snackbar.LENGTH_SHORT).show();
                    materialFavoriteButton.setAnimateFavorite(true);
                });
        }

        return view;
    }

    private void fetchCrewAndSimilarShowDetails(Integer id) {
        characterResultObservable = retrofit.create(ApiInterface.class)
                .getCrew(id, BuildConfig.TV_KEY);
        characterResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(characterResult -> {
                    castList = characterResult.getCast();
                    return retrofit.create(ApiInterface.class).getSimilarShows(id, BuildConfig.TV_KEY)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }).subscribe(new Observer<SimilarShowResults>() {


            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(SimilarShowResults similarShowResults) {
                similarShowDetails = similarShowResults.getResults();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                characterListAdapter = new CharacterListAdapter(getActivity(), castList);
                characterLayoutManager = new LinearLayoutManager(getActivity());
                characterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                characterRecyclerView.setLayoutManager(characterLayoutManager);
                characterRecyclerView.setAdapter(characterListAdapter);
                similarShowsAdapter = new SimilarShowsAdapter(getActivity(), similarShowDetails);
                similarShowLayoutManager = new LinearLayoutManager(getActivity());
                similarShowLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                similarShowsRecyclerView.setLayoutManager(similarShowLayoutManager);
                similarShowsRecyclerView.setAdapter(similarShowsAdapter);
            }
        });
    }


}
