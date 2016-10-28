package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.etc.ObjectToJson;

/**
 * Created by Developer on 10/06/2016.
 */
public abstract class Location implements ObjectToJson{

    @SerializedName("idWeb")
    @Expose
    protected String id;

    @SerializedName("description")
    @Expose
    protected String location;
    protected LocationType locationType;


    @SerializedName("type")
    @Expose
    private String type;

    public Location(){}

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

        if(type != null){
            return LocationType.values()[Integer.valueOf(type)];
        }
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
