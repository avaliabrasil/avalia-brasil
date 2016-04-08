
package org.avaliabrasil.avaliabrasil.rest.javabeans;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceRankingSearch {

    @SerializedName("places")
    @Expose
    private List<PlaceRanking> placeRankings = new ArrayList<PlaceRanking>();

    /**
     * 
     * @return
     *     The placeRankings
     */
    public List<PlaceRanking> getPlaceRankings() {
        return placeRankings;
    }

    /**
     * 
     * @param placeRankings
     *     The placeRankings
     */
    public void setPlaceRankings(List<PlaceRanking> placeRankings) {
        this.placeRankings = placeRankings;
    }

}
