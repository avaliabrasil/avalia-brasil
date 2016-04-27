
package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AccessPoint {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("travel_modes")
    @Expose
    private List<String> travelModes = new ArrayList<String>();

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

    /**
     * 
     * @return
     *     The travelModes
     */
    public List<String> getTravelModes() {
        return travelModes;
    }

    /**
     * 
     * @param travelModes
     *     The travel_modes
     */
    public void setTravelModes(List<String> travelModes) {
        this.travelModes = travelModes;
    }

}
