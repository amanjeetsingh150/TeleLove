package com.developers.telelove.model.PopularShowsModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Amanjeet Singh on 25/12/17.
 */

public class PopularResultData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poster")
    @Expose
    private String posterPath;
    @SerializedName("release")
    @Expose
    private String releaseDate;
    @SerializedName("rate")
    @Expose
    private String rating;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("backdrop_img")
    @Expose
    private String backDropImagePath;
    @SerializedName("trailer_path")
    @Expose
    private String trailer;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBackDropImagePath() {
        return backDropImagePath;
    }

    public void setBackDropImagePath(String backDropImagePath) {
        this.backDropImagePath = backDropImagePath;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }


}
