package org.avaliabrasil.avaliabrasil.avb.javabeans.ranking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RankingStatus {

    @SerializedName("national")
    @Expose
    private String national;
    @SerializedName("regional")
    @Expose
    private String regional;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("municipal")
    @Expose
    private String municipal;

    /**
     * @return The national
     */
    public String getNational() {
        return national;
    }

    /**
     * @param national The national
     */
    public void setNational(String national) {
        this.national = national;
    }

    /**
     * @return The regional
     */
    public String getRegional() {
        return regional;
    }

    /**
     * @param regional The regional
     */
    public void setRegional(String regional) {
        this.regional = regional;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return The municipal
     */
    public String getMunicipal() {
        return municipal;
    }

    /**
     * @param municipal The municipal
     */
    public void setMunicipal(String municipal) {
        this.municipal = municipal;
    }

}
