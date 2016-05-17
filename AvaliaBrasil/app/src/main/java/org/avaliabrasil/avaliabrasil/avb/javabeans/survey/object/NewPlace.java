package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object;

/**
 * Created by Developer on 12/05/2016.
 */
public class NewPlace {

    private String placeId;
    private String placeCategory;
    private String placeType;

    public NewPlace() {
    }

    public NewPlace(String placeId, String placeCategory, String placeType) {
        this.placeId = placeId;
        this.placeCategory = placeCategory;
        this.placeType = placeType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceCategory() {
        return placeCategory;
    }

    public void setPlaceCategory(String placeCategory) {
        this.placeCategory = placeCategory;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }
}
