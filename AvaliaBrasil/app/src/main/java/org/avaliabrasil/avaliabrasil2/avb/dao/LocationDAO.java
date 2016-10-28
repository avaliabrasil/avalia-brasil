package org.avaliabrasil.avaliabrasil2.avb.dao;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationCity;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationType;

import java.util.List;

/**
 * Created by Developer on 10/06/2016.
 */
public interface LocationDAO<T> {

    public void bulkAddLocation(List<T> locationList);
    public void addLocation(Location location);
    public List<Location> findLocationByName(String name);
    public Location findLocationByWebID(String webId, LocationType locationType);
    public boolean isEmpty();
}
