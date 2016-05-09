package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OpeningHour {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;

    @SerializedName("periods")
    @Expose
    private List<Period> periods = new ArrayList<Period>();

    /**
     * @return The openNow
     */
    public Boolean getOpenNow() {
        return openNow;
    }

    /**
     * @param openNow The open_now
     */
    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    /**
     * @return The periods
     */
    public List<Period> getPeriods() {
        return periods;
    }

    /**
     * @param periods The periods
     */
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

}
