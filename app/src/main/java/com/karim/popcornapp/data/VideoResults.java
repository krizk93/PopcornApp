package com.karim.popcornapp.data;

/**
 * Created by Karim on 02-Apr-18.
 */

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoResults {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Videos> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Videos> getResults() {
        return results;
    }

    public void setResults(List<Videos> results) {
        this.results = results;
    }

}
