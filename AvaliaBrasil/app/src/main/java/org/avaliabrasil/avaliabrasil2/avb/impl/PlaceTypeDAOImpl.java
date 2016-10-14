package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.content.ContentValues;
import android.content.Context;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlaceTypeDAO;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilPlaceType;

import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public class PlaceTypeDAOImpl implements PlaceTypeDAO{

    private Context context;

    public PlaceTypeDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public void bulkInsertPlaceType(List<AvaliaBrasilPlaceType> types) {

        ContentValues[] values = new ContentValues[types.size()];
        ContentValues value = null;

        if (types.size() > 0) {
            values = new ContentValues[types.size()];

            for (int i = 0; i < types.size(); i++) {
                value = new ContentValues();
                value.put(AvBContract.PlaceTypeEntry._ID, types.get(i).getId());
                value.put(AvBContract.PlaceTypeEntry.CATEGORY_ID, types.get(i).getIdCategory());
                value.put(AvBContract.PlaceTypeEntry.NAME, types.get(i).getCategory());
                values[i] = value;
            }

            context.getContentResolver().bulkInsert(
                    AvBContract.PlaceTypeEntry.PLACE_TYPE_URI, values);
        }
    }
}
