package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceStatistics {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("qualityIndex")
    @Expose
    private List<QualityIndex> qualityIndex = new ArrayList<QualityIndex>();
    @SerializedName("rankingPosition")
    @Expose
    private RankingPosition rankingPosition;
    @SerializedName("rankingStatus")
    @Expose
    private RankingStatus rankingStatus;
    @SerializedName("lastWeekSurveys")
    @Expose
    private Integer lastWeekSurveys;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = new ArrayList<Comment>();
    @SerializedName("id_city")
    @Expose
    private String id_city;
    @SerializedName("id_state")
    @Expose
    private String id_state;
    @SerializedName("id_region")
    @Expose
    private String id_region;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
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
     * @return The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The qualityIndex
     */
    public List<QualityIndex> getQualityIndex() {
        return qualityIndex;
    }

    /**
     * @param qualityIndex The qualityIndex
     */
    public void setQualityIndex(List<QualityIndex> qualityIndex) {
        this.qualityIndex = qualityIndex;
    }

    /**
     * @return The rankingPosition
     */
    public RankingPosition getRankingPosition() {
        return rankingPosition;
    }

    /**
     * @param rankingPosition The rankingPosition
     */
    public void setRankingPosition(RankingPosition rankingPosition) {
        this.rankingPosition = rankingPosition;
    }

    /**
     * @return The rankingStatus
     */
    public RankingStatus getRankingStatus() {
        return rankingStatus;
    }

    /**
     * @param rankingStatus The rankingStatus
     */
    public void setRankingStatus(RankingStatus rankingStatus) {
        this.rankingStatus = rankingStatus;
    }

    /**
     * @return The lastWeekSurveys
     */
    public Integer getLastWeekSurveys() {
        return lastWeekSurveys;
    }

    /**
     * @param lastWeekSurveys The lastWeekSurveys
     */
    public void setLastWeekSurveys(Integer lastWeekSurveys) {
        this.lastWeekSurveys = lastWeekSurveys;
    }

    /**
     * @return The comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * @param comments The comments
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getId_city() {
        return id_city;
    }

    public void setId_city(String id_city) {
        this.id_city = id_city;
    }

    public String getId_state() {
        return id_state;
    }

    public void setId_state(String id_state) {
        this.id_state = id_state;
    }

    public String getId_region() {
        return id_region;
    }

    public void setId_region(String id_region) {
        this.id_region = id_region;
    }
}
