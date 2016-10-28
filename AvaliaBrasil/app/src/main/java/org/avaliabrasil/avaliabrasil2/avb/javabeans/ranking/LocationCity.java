package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import android.database.Cursor;

import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationCity extends Location{

    @Deprecated
    private String idState;
    @Deprecated
    private String state;

    @Deprecated
    public LocationCity(String id, String location,String idState, String state) {
        super(id, location, LocationType.CITY);
        this.idState = idState;
        this.state = state;
    }

    public LocationCity(Cursor c) {
        super(c.getString(c.getColumnIndex(AvBContract.LocationEntry.IDWEB)),c.getString(c.getColumnIndex(AvBContract.LocationEntry.DESCRIPTION)), LocationType.CITY);
    }

    @Deprecated
    public String getIdState() {
        return idState;
    }

    @Deprecated
    public void setIdState(String idState) {
        this.idState = idState;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = super.toJson();
        //obj.addProperty("id_state",idState);

        return obj;
    }

    @Override
    public String toString() {
        return location;
    }
}
