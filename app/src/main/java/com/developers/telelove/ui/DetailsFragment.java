package com.developers.telelove.ui;


import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.developers.telelove.data.ShowContract;
import com.developers.telelove.model.CharactersModel.Cast;
import com.developers.telelove.model.CharactersModel.CharacterResult;
import com.developers.telelove.model.FavouriteShowsResult;
import com.developers.telelove.model.PopularShowsModel.PopularResultData;
import com.developers.telelove.model.PopularShowsModel.Result;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowDetails;
import com.developers.telelove.model.SimilarShowsResult.SimilarShowResults;
import com.developers.telelove.model.TopRatedShowsModel.TopRatedDetailResults;
import com.developers.telelove.model.VideosModel.VideoDetailResult;
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
    String detailsJson, preference, videoURL;
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
    SimilarShowsAdapter similarShowsAdapter;
    @BindView(R.id.similar_shows_recycler_view)
    RecyclerView similarShowsRecyclerView;
    LinearLayoutManager characterLayoutManager, similarShowLayoutManager;
    Observable<VideoResult> videoResultObservable;
    Observable<CharacterResult> characterResultObservable;
    List<SimilarShowDetails> similarShowDetails;
    List<VideoDetailResult> videoDetailResults;
    Uri posterUri, backDropUri;
    String videoPath, similarShowsJson, characterJson;
    private Result popularResultData;
    private TopRatedDetailResults ratedDetailResults;
    private CharacterListAdapter characterListAdapter;
    private List<Cast> castList;
    private FavouriteShowsResult favouriteShowsResult;


    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getNetComponent().inject(this);
        boolean isTabUsed = getActivity().getResources().getBoolean(R.bool.tab);
        Bundle bundle = getArguments();
        gson = new Gson();
        preference = sharedPreferences.getString(getActivity().getString(R.string.preferences_key),
                "0");
        Log.d(TAG, " " + preference + detailsJson);
        switch (preference) {
            case "0":
                if (!isTabUsed) {
                    detailsJson = bundle.getString(Constants.KEY_DETAILS);
                    popularResultData = gson.fromJson(detailsJson, Result.class);
                } else {
                    detailsJson = bundle.getString(Constants.KEY_POPULAR_SHOWS);
                    popularResultData = gson.fromJson(detailsJson, Result.class);
                }
                break;
            case "1":
                if (!isTabUsed) {
                    detailsJson = bundle.getString(Constants.KEY_DETAILS);
                    ratedDetailResults = gson.fromJson(detailsJson, TopRatedDetailResults.class);
                } else {
                    detailsJson = bundle.getString(Constants.KEY_TOP_RATED);
                    ratedDetailResults = gson.fromJson(detailsJson, TopRatedDetailResults.class);
                }
                break;
            case "2":
                if (!isTabUsed) {
                    detailsJson = bundle.getString(Constants.KEY_DETAILS);
                    favouriteShowsResult = gson.fromJson(detailsJson, FavouriteShowsResult.class);
                } else {
                    detailsJson = bundle.getString(Constants.KEY_FAVOURITES);
                    favouriteShowsResult = gson.fromJson(detailsJson, FavouriteShowsResult.class);
                }
                break;
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
                posterUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                        .appendEncodedPath(popularResultData.getPosterPath()).build();
                boolean present = checkIfPresentInDB(popularResultData.getId());
                if (present) {
                    materialFavoriteButton.setFavorite(true, false);
                }
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
                videoURL = getVideoPath(popularResultData.getId());
                backDropUri = Uri.parse(Constants.BASE_URL_IMAGES)
                        .buildUpon().appendEncodedPath(popularResultData.getBackdropPath())
                        .build();
                Picasso.with(getActivity()).load(backDropUri.toString())
                        .into(backDropImage);
                fetchCrewAndSimilarShowDetails(popularResultData.getId());
                titleTextView.setText(popularResultData.getName());
                overviewTextView.setText(popularResultData.getOverview());
                ratingText.setText(String.valueOf(popularResultData.getVoteAverage()));
                materialFavoriteButton.setOnFavoriteChangeListener((buttonView, favorite) -> {
                    if (favorite) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_ID,
                                popularResultData.getId());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_TITLE,
                                popularResultData.getName());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_POSTER,
                                posterUri.toString());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_RELEASE_DATE,
                                popularResultData.getFirstAirDate());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_VOTE_AVERAGE,
                                popularResultData.getVoteAverage());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_OVERVIEW,
                                popularResultData.getOverview());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_TRAILER,
                                videoURL);
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_BACKDROP_IMG,
                                backDropUri.toString());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_CHARACTERS,
                                characterJson);
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_SIMILAR_SHOWS,
                                similarShowsJson);
                        getActivity().getContentResolver().insert(ShowContract.FavouriteShows.uri,
                                contentValues);
                        Snackbar.make(view, getActivity().getString(R.string.added_favorites),
                                Snackbar.LENGTH_SHORT).show();
                    }
                    if (!favorite) {
                        String deleteId[] = new String[]{String.valueOf(popularResultData.getId())};
                        getActivity().getContentResolver().delete(ShowContract.FavouriteShows.uri,
                                ShowContract.FavouriteShows.COLUMN_ID + " =?", deleteId);
                        Snackbar.make(view, getActivity().getString(R.string.removed_favourites)
                                , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case "1":
                Uri topRatedPosterUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                        .appendEncodedPath(ratedDetailResults.getPosterPath()).build();
                boolean ratedPresent = checkIfPresentInDB(ratedDetailResults.getId());
                if (ratedPresent) {
                    materialFavoriteButton.setFavorite(true, false);
                }
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
                materialFavoriteButton.setOnFavoriteChangeListener((buttonView, favorite) -> {
                    if (favorite) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_ID,
                                ratedDetailResults.getId());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_TITLE,
                                ratedDetailResults.getName());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_POSTER,
                                topRatedPosterUri.toString());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_RELEASE_DATE,
                                ratedDetailResults.getFirstAirDate());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_VOTE_AVERAGE,
                                ratedDetailResults.getVoteAverage());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_OVERVIEW,
                                ratedDetailResults.getOverview());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_TRAILER,
                                videoURL);
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_BACKDROP_IMG,
                                topRatedBackDropUri.toString());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_CHARACTERS,
                                characterJson);
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_SIMILAR_SHOWS,
                                similarShowsJson);
                        getActivity().getContentResolver().insert(ShowContract.FavouriteShows.uri,
                                contentValues);
                        Snackbar.make(view, getActivity().getString(R.string.added_favorites),
                                Snackbar.LENGTH_SHORT).show();
                    }
                    if (!favorite) {
                        String deleteId[] = new String[]{String.valueOf(ratedDetailResults.getId())};
                        getActivity().getContentResolver().delete(ShowContract.FavouriteShows.uri,
                                ShowContract.FavouriteShows.COLUMN_ID + " =?", deleteId);
                        Snackbar.make(view, getActivity().getString(R.string.removed_favourites)
                                , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case "2":
                Uri favouritePosterUri = Uri.parse(Constants.BASE_URL_IMAGES).buildUpon()
                        .appendEncodedPath(favouriteShowsResult.getPosterPath()).build();
                boolean favouritePresent = checkIfPresentInDB(Integer
                        .parseInt(favouriteShowsResult.getId()));
                if (favouritePresent) {
                    materialFavoriteButton.setFavorite(true, false);
                }
                Picasso.with(getActivity()).load(favouritePosterUri.toString())
                        .into(posterImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                Uri favouriteShowBackDropUri = Uri.parse(Constants.BASE_URL_IMAGES)
                        .buildUpon().appendEncodedPath(favouriteShowsResult.getBackDropImagePath())
                        .build();
                Picasso.with(getActivity()).load(favouriteShowBackDropUri.toString())
                        .into(backDropImage);
                fetchCrewAndSimilarShowDetails(Integer.parseInt(favouriteShowsResult.getId()));
                titleTextView.setText(favouriteShowsResult.getTitle());
                overviewTextView.setText(favouriteShowsResult.getOverview());
                ratingText.setText(String.valueOf(favouriteShowsResult.getRating()));
                materialFavoriteButton.setOnFavoriteChangeListener((buttonView, favorite) -> {
                    if (favorite) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_ID,
                                favouriteShowsResult.getId());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_TITLE,
                                favouriteShowsResult.getTitle());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_POSTER,
                                favouritePosterUri.toString());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_RELEASE_DATE,
                                favouriteShowsResult.getReleaseDate());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_VOTE_AVERAGE,
                                favouriteShowsResult.getRating());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_OVERVIEW,
                                favouriteShowsResult.getOverview());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_TRAILER,
                                videoURL);
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_BACKDROP_IMG,
                                favouriteShowBackDropUri.toString());
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_CHARACTERS,
                                characterJson);
                        contentValues.put(ShowContract.FavouriteShows.COLUMN_SIMILAR_SHOWS,
                                similarShowsJson);
                        getActivity().getContentResolver().insert(ShowContract.FavouriteShows.uri,
                                contentValues);
                        Snackbar.make(view, getActivity().getString(R.string.added_favorites),
                                Snackbar.LENGTH_SHORT).show();
                    }
                    if (!favorite) {
                        String deleteId[] = new String[]{String.valueOf(favouriteShowsResult.getId())};
                        getActivity().getContentResolver().delete(ShowContract.FavouriteShows.uri,
                                ShowContract.FavouriteShows.COLUMN_ID + " =?", deleteId);
                        Snackbar.make(view, getActivity().getString(R.string.removed_favourites)
                                , Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
        }
        return view;
    }

    private boolean checkIfPresentInDB(int id) {
        String showId[] = new String[]{String.valueOf(id)};
        Cursor cursor = getActivity().getContentResolver().query(ShowContract.FavouriteShows.uri,
                ShowContract.FavouriteShows.projectionsForMainActivity,
                ShowContract.FavouriteShows.COLUMN_ID + " =?", showId, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    private void fetchCrewAndSimilarShowDetails(Integer id) {
        characterResultObservable = retrofit.create(ApiInterface.class)
                .getCrew(id, BuildConfig.TV_KEY);
        characterResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(characterResult -> {
                    castList = characterResult.getCast();
                    characterJson = gson.toJson(castList);
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
                similarShowsJson = gson.toJson(similarShowDetails);
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

    private String getVideoPath(int id) {
        videoResultObservable = retrofit.create(ApiInterface.class)
                .getTrailers(id, BuildConfig.TV_KEY);
        videoResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoResult>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(VideoResult videoResult) {
                        videoDetailResults = videoResult.getResults();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        if (!(disposable.isDisposed())) {
                            disposable.dispose();
                        }
                        if (videoDetailResults != null) {
                            if (videoDetailResults.size() > 0) {
                                for (VideoDetailResult videoDetailResult : videoDetailResults) {
                                    String key = videoDetailResult.getKey();
                                    if (key != null) {
                                        if (key.length() > 0) {
                                            Uri videoUri = Uri.parse(Constants.YOUTUBE_BASE_URL)
                                                    .buildUpon()
                                                    .appendQueryParameter("v", key).build();
                                            videoPath = videoUri.toString();
                                            break;
                                        }
                                    }
                                }
                            } else {
                                videoPath = getActivity()
                                        .getString(R.string.trailer_not_available_error);
                            }
                        } else {
                            videoPath = getActivity()
                                    .getString(R.string.trailer_not_available_error);
                        }
                    }
                });
        return videoPath;
    }


}
