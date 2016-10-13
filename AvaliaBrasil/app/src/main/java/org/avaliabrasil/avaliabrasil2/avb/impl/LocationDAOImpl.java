package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.LocationDAO;
import org.avaliabrasil.avaliabrasil2.avb.factory.LocationFactory;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationRegion;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationResponse;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationDAOImpl implements LocationDAO<LocationResponse> {

    private Context context;
    private LocationFactory<Cursor> locationFactory;

    public LocationDAOImpl(Context context,LocationFactory<Cursor> locationFactory) {
        this.context = context;
        this.locationFactory = locationFactory;
    }

    @Override
    public void bulkAddLocation(List<LocationResponse> locationList) {

        ContentValues[] values = new ContentValues[locationList.size()];
        ContentValues value = null;

        for (int i = 0; i < locationList.size(); i++) {
                value = new ContentValues();
                value.put(AvBContract.LocationEntry.IDWEB, locationList.get(i).getId());
                value.put(AvBContract.LocationEntry.TYPE, locationList.get(i).getType());
                value.put(AvBContract.LocationEntry.DESCRIPTION, locationList.get(i).getLocation());
                values[i] = value;
        }
        context.getContentResolver().bulkInsert(
                AvBContract.LocationEntry.LOCATION_URI, values);

    }

    @Override
    public void addLocation(Location location) {

        ContentValues value = null;

            value = new ContentValues();
            value.put(AvBContract.LocationEntry.IDWEB,location.getId());
            value.put(AvBContract.LocationEntry.TYPE, location.getLocationType().ordinal());
            value.put(AvBContract.LocationEntry.DESCRIPTION, location.getLocation());


        context.getContentResolver().insert(
                AvBContract.LocationEntry.LOCATION_URI, value);

    }

    @Override
    public List<Location> findLocationByName(String name) {

        Log.d("LocationDAO","filter: "  + name);

        List<Location> locationsList = new ArrayList<>();

        Cursor c = context.getContentResolver().query(AvBContract.LocationEntry.getFilteredLocation(name), null, null, null, null);

        Location loc = null;
        Log.d("LocationDAO","response size: "  + c.getCount());

        while (c.moveToNext()) {
            loc = locationFactory.getLocationByType(c);
            if(loc != null){
                locationsList.add(loc);
            }
        }

        c.close();

        return locationsList;
    }

    @Override
    public Location findLocationByWebID(String webId) {
        if(webId.isEmpty()){
            webId = "31";
        }
        Cursor c = context.getContentResolver().query(AvBContract.LocationEntry.getById(webId), null, null, null, null);
        return locationFactory.getLocationByType(c);
    }

    @Override
    public boolean isEmpty() {
        Cursor c = context.getContentResolver().query(AvBContract.LocationEntry.LOCATION_URI, null, null, null, null);
        boolean isEmpty = c.getCount() == 0;
        c.close();

        Log.d("LocationDAOImpl", "isEmpty: " + isEmpty);
        return isEmpty;
    }
}
