package org.avaliabrasil.avaliabrasil.avb.javabeans.ranking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RankingPosition {

    @SerializedName("national")
    @Expose
    private Integer national;
    @SerializedName("regional")
    @Expose
    private Integer regional;
    @SerializedName("state")
    @Expose
    private Integer state;
    @SerializedName("municipal")
    @Expose
    private Integer municipal;

    /**
     * @return The national
     */
    public Integer getNational() {
        return national;
    }

    /**
     * @param national The national
     */
    public void setNational(Integer national) {
        this.national = national;
    }

    /**
     * @return The regional
     */
    public Integer getRegional() {
        return regional;
    }

    /**
     * @param regional The regional
     */
    public void setRegional(Integer regional) {
        this.regional = regional;
    }

    /**
     * @return The state
     */
    public Integer getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * @return The municipal
     */
    public Integer getMunicipal() {
        return municipal;
    }

    /**
     * @param municipal The municipal
     */
    public void setMunicipal(Integer municipal) {
        this.municipal = municipal;
    }

}
