package org.avaliabrasil.avaliabrasil.avb.javabeans.ranking;

import com.google.gson.JsonObject;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationState extends Location {

    private String countryId;
    private String regionId;
    private String region;

    public LocationState(String id, String location,String countryId,String regionId, String region) {
        super(id, location, LocationType.STATE);
        this.countryId = countryId;
        this.regionId = regionId;
        this.region = region;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }


    @Override
    public JsonObject toJson() {
        JsonObject obj =  super.toJson();
        obj.addProperty("country_id",countryId);
        obj.addProperty("region_id",regionId);
        return obj;
    }

    @Override
    public String toString() {
        return location.concat(" ,").concat(region);
    }
}
