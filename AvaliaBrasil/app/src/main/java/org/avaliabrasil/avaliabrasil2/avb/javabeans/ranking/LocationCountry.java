package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import android.database.Cursor;

import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationCountry extends Location {

    private String isoCode;

    public LocationCountry(String id, String location, String isoCode) {
        super(id, location, LocationType.COUNTRY);
        this.isoCode = isoCode;
    }

    public LocationCountry(Cursor c) {
        super(c.getString(c.getColumnIndex(AvBContract.LocationEntry.IDWEB)),c.getString(c.getColumnIndex(AvBContract.LocationEntry.DESCRIPTION)), LocationType.COUNTRY);
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
        //obj.addProperty("iso_code",isoCode);
        return obj;
    }
}
