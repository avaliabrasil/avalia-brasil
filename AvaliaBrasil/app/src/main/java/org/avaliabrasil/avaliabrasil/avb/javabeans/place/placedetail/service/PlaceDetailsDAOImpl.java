package org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.service;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.dao.PlaceDetailsDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.object.ResultDetails;

/**
 * Created by Developer on 13/05/2016.
 */
public class PlaceDetailsDAOImpl implements PlaceDetailsDAO {

    private Context context;

    public PlaceDetailsDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    @Nullable
    public ResultDetails getPlaceDetailsByPlaceId(String place_id) {
        ResultDetails resultDetails = null;

        Cursor c = context.getContentResolver().query(
                AvBContract.PlaceEntry.getPlaceDetails(place_id), null, null, null, null);

        if(c.moveToNext()){
            resultDetails = new ResultDetails();
        }

        return resultDetails;
    }
}
