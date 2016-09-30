package org.avaliabrasil.avaliabrasil.avb.impl;

import android.util.Log;

import org.avaliabrasil.avaliabrasil.avb.dao.LocationDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.Location;
import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.LocationCity;
import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.LocationCountry;
import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.LocationRegion;
import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.LocationState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationDAOTestImpl implements LocationDAO {

    private List<Location> locationList;

    public LocationDAOTestImpl() {
        locationList = new ArrayList<>();
        /** Countries */
        locationList.add(new LocationCountry("31","Brasil","BR"));

        /** Regions */
        locationList.add(new LocationRegion("1","Norte"));
        locationList.add(new LocationRegion("2","Nordeste"));
        locationList.add(new LocationRegion("3","Sudeste"));
        locationList.add(new LocationRegion("4","Sul"));
        locationList.add(new LocationRegion("5","Centro-Oeste"));

        /** States */
        locationList.add(new LocationState("23","Rio Grande do Sul","31","4","Sul"));
        locationList.add(new LocationState("24","Santa Catarina","31","4","Sul"));

        /** Cities */
        locationList.add(new LocationCity("4889","Novo Hamburgo","23","RS"));
        locationList.add(new LocationCity("5000","São Leopoldo","23","RS"));
        locationList.add(new LocationCity("4322","Anita Garibaldi","24","SC"));

    }

    public List<Location> getLocationList() {
        return locationList;
    }

    @Override
    public void bulkAddLocation(List<Location> locationList) {
        this.locationList.addAll(locationList);
    }

    @Override
    public void addLocation(Location location) {
        this.locationList.add(location);
    }

    @Override
    public List<Location> findLocationByName(String name) {

        Log.d("LocationDAO","filter: "  + name);

        ArrayList<Location> findedLocations = new ArrayList<Location>();

        for (Location location : locationList) {
            if (location.getLocation().toLowerCase().contains(name.toLowerCase())) {
                findedLocations.add(location);
            }
        }

        Log.d("LocationDAO","size: "  + findedLocations.size());

        return findedLocations;
    }
}
