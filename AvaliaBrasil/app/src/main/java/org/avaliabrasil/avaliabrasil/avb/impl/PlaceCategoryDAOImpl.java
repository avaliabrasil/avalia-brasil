package org.avaliabrasil.avaliabrasil.avb.impl;

import android.content.ContentValues;
import android.content.Context;

import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.dao.PlaceCategoryDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.AvaliaBrasilCategory;

import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public class PlaceCategoryDAOImpl implements PlaceCategoryDAO{

    private Context context;

    public PlaceCategoryDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public void bulkInsertPlaceCategory(List<AvaliaBrasilCategory> categories) {
        ContentValues[] values = null;
        ContentValues value = null;

        if (categories.size() > 0) {
            values = new ContentValues[categories.size()];

            for (int i = 0; i < categories.size(); i++) {
                value = new ContentValues();
                value.put(AvBContract.PlaceCategoryEntry.CATEGORY_ID, categories.get(i).getIdCategory());
                value.put(AvBContract.PlaceCategoryEntry.NAME, categories.get(i).getCategory());
                values[i] = value;
            }

            context.getContentResolver().bulkInsert(
                    AvBContract.PlaceCategoryEntry.PLACE_CATEGORY_URI, values);
        }
    }
}
