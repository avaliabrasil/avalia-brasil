package org.avaliabrasil.avaliabrasil.avb.javabeans.ranking;

import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.avb.javabeans.etc.ObjectToJson;

/**
 * Created by Developer on 10/06/2016.
 */
public abstract class Location implements ObjectToJson{

    protected String id;
    protected String location;
    protected LocationType locationType;

    public Location(String id, String location, LocationType locationType) {
        this.id = id;
        this.location = location;
        this.locationType = locationType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    @Override
    public String toString() {
        return location;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();

        obj.addProperty("id",id);
        obj.addProperty("type",locationType.name());
        obj.addProperty("location",location);

        return obj;
    }
}
