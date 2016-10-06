package org.avaliabrasil.avaliabrasil2.avb.javabeans.google.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GoogleGeoCodeReverse {

    @SerializedName("results")
    @Expose
    private List<GoogleGeoCodeResult> results = new ArrayList<GoogleGeoCodeResult>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     * @return The results
     */
    public List<GoogleGeoCodeResult> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(List<GoogleGeoCodeResult> results) {
        this.results = results;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
