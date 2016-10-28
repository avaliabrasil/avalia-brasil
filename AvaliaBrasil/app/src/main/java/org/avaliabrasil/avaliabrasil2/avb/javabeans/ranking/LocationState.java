package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import android.database.Cursor;

import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;

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

    public LocationState(Cursor c) {
        super(c.getString(c.getColumnIndex(AvBContract.LocationEntry.IDWEB)),c.getString(c.getColumnIndex(AvBContract.LocationEntry.DESCRIPTION)), LocationType.STATE);
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
        return location;
    }
}
