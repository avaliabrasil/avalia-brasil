
package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceRanking {

    @SerializedName("rankingatual")
    @Expose
    private Integer rankingPosition;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("qualityIndex")
    @Expose
    private Double qualityIndex;

    /**
     * 
     * @return
     *     The rankingPosition
     */
    public Integer getRankingPosition() {
        return rankingPosition;
    }

    /**
     * 
     * @param rankingPosition
     *     The rankingPosition
     */
    public void setRankingPosition(Integer rankingPosition) {
        this.rankingPosition = rankingPosition;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 
     * @return
     *     The qualityIndex
     */
    public Double getQualityIndex() {
        return qualityIndex;
    }

    /**
     * 
     * @param qualityIndex
     *     The qualityIndex
     */
    public void setQualityIndex(Double qualityIndex) {
        this.qualityIndex = qualityIndex;
    }

}
