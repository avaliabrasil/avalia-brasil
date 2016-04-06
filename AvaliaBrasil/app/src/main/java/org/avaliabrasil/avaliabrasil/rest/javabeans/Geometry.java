
package org.avaliabrasil.avaliabrasil.rest.javabeans;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("access_points")
    @Expose
    private List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
    @SerializedName("location")
    @Expose
    private Location location;

    /**
     * 
     * @return
     *     The accessPoints
     */
    public List<AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    /**
     * 
     * @param accessPoints
     *     The access_points
     */
    public void setAccessPoints(List<AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }

    /**
     * 
     * @return
     *     The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

}
