package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="https://github.com/Klauswk">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */

public class LocationResponse {

    @SerializedName("idWeb")
    @Expose
    String id;

    @SerializedName("description")
    @Expose
    String location;

    @SerializedName("type")
    @Expose
    String type;

    public LocationResponse(){}

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
