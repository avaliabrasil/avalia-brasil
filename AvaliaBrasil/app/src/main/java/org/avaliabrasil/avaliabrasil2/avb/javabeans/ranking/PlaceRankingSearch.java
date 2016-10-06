package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceRankingSearch {

    @SerializedName("places")
    @Expose
    private List<PlaceRanking> placeRankings = new ArrayList<PlaceRanking>();

    /**
     * @return The placeRankings
     */
    public List<PlaceRanking> getPlaceRankings() {
        return placeRankings;
    }

    /**
     * @param placeRankings The placeRankings
     */
    public void setPlaceRankings(List<PlaceRanking> placeRankings) {
        this.placeRankings = placeRankings;
    }

}
