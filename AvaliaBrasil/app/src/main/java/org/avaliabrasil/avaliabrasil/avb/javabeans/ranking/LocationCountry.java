package org.avaliabrasil.avaliabrasil.avb.javabeans.ranking;

import com.google.gson.JsonObject;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationCountry extends Location {

    private String isoCode;

    public LocationCountry(String id, String location, String isoCode) {
        super(id, location, LocationType.COUNTRY);
        this.isoCode = isoCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = super.toJson();
        obj.addProperty("iso_code",isoCode);
        return obj;
    }
}
