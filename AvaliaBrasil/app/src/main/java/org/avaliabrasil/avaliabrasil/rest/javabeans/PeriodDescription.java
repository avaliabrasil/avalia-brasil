package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 06/05/2016.
 */
public class PeriodDescription {

    @SerializedName("day")
    @Expose
    private Integer day;
    @SerializedName("time")
    @Expose
    private String time;

    /**
     * @return The day
     */
    public Integer getDay() {
        return day;
    }

    /**
     * @param day The day
     */
    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     * @return The time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time The time
     */
    public void setTime(String time) {
        this.time = time;
    }
}
