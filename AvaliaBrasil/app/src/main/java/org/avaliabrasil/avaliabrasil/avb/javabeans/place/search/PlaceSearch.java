package org.avaliabrasil.avaliabrasil.avb.javabeans.place.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *         <p/>
 *         This class act has a javabean to Google Places API find places URI, by mapping the {@link com.google.gson.JsonObject} creating a
 *         java class that represent the response.
 * @version 1.0
 * @see <a href="https://en.wikipedia.org/wiki/JavaBeans">JavaBean</a>
 * @see <a href="https://en.wikipedia.org/wiki/Representational_state_transfer">Restfull</a>.
 * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
 * @since 1.0
 */
public class PlaceSearch {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = new ArrayList<Object>();
    @SerializedName("results")
    @Expose
    private List<ResultPlaceSearch> results = new ArrayList<ResultPlaceSearch>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     * @return The htmlAttributions
     */
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    /**
     * @param htmlAttributions The html_attributions
     */
    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    /**
     * @return The results
     */
    public List<ResultPlaceSearch> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(List<ResultPlaceSearch> results) {
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
