package org.avaliabrasil.avaliabrasil.avb.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.Location;

import java.util.List;

/**
 * Created by Developer on 10/06/2016.
 */
public interface LocationDAO {

    public void bulkAddLocation(List<Location> locationList);
    public void addLocation(Location location);
    public List<Location> findLocationByName(String name);


}
