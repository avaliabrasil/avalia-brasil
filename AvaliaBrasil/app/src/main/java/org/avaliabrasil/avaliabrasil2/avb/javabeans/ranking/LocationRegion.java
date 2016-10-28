package org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking;

import android.database.Cursor;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;

/**
 * Created by Developer on 10/06/2016.
 */
public class LocationRegion extends Location{

    public LocationRegion(String id, String location) {
        super(id, location, LocationType.REGION);
    }

    public LocationRegion(Cursor c) {
        super(c.getString(c.getColumnIndex(AvBContract.LocationEntry.IDWEB)),c.getString(c.getColumnIndex(AvBContract.LocationEntry.DESCRIPTION)), LocationType.REGION);
    }
}
