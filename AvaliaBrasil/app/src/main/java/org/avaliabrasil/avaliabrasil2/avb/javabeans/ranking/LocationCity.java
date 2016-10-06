package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import com.google.gson.JsonObject;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationCity extends Location{

    private String idState;
    private String state;

    public LocationCity(String id, String location,String idState, String state) {
        super(id, location, LocationType.CITY);
        this.idState = idState;
        this.state = state;
    }

    public String getIdState() {
        return idState;
    }

    public void setIdState(String idState) {
        this.idState = idState;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = super.toJson();
        obj.addProperty("id_state",idState);

        return obj;
    }

    @Override
    public String toString() {
        return location.concat(",").concat(state);
    }
}
