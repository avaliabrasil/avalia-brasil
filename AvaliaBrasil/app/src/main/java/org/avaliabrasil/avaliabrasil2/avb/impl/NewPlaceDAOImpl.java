package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.NewPlaceDAO;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.NewPlace;

/**
 * Created by Developer on 12/05/2016.
 */
public class NewPlaceDAOImpl implements NewPlaceDAO {

    private Context context;

    public NewPlaceDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public void insertNewPlace(NewPlace newPlace) {
        ContentValues cv = new ContentValues();

        cv.put(AvBContract.NewPlaceEntry.PLACE_ID, newPlace.getPlaceId());
        cv.put(AvBContract.NewPlaceEntry.CATEGORY_ID, newPlace.getPlaceCategory());
        cv.put(AvBContract.NewPlaceEntry.PLACE_TYPE_ID, newPlace.getPlaceType());

        context.getContentResolver().insert(AvBContract.NewPlaceEntry.NEWPLACE_URI, cv);
    }

    @Override
    public void deleteNewPlaceByPlaceId(String placeId) {
        context.getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, AvBContract.NewPlaceEntry.PLACE_ID + " = ?", new String[]{placeId});
    }

    @Override
    public NewPlace findNewPlaceByPlaceId(String placeId) {
        Cursor c = context.getContentResolver().query(AvBContract.NewPlaceEntry.NEWPLACE_URI, null, "place_id = ?", new String[]{placeId}, null);
        NewPlace newPlace = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            newPlace = new NewPlace(placeId,c.getString(c.getColumnIndex(AvBContract.NewPlaceEntry.CATEGORY_ID)),c.getString(c.getColumnIndex(AvBContract.NewPlaceEntry.PLACE_TYPE_ID)));
        }

        c.close();
        return newPlace;
    }
}
