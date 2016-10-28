package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlacePeriodDAO;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.google.places.OpeningHour;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.google.places.Period;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.google.places.PeriodDescription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Developer on 19/05/2016.
 */
public class PlacePeriodDAOImpl implements PlacePeriodDAO {

    private Context context;

    public PlacePeriodDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<Period> findPeriodsPlaceId(String placeId) {
        Cursor c = context.getContentResolver().query(
                AvBContract.PlacePeriodEntry.buildPlacePeriodUri(placeId), null, null, null, null);

        List<Period> periods = new ArrayList<Period>();
        List<PeriodDescription> periodDescriptions = new ArrayList<>();
        Period period;
        PeriodDescription periodDescription;

        while(c.moveToNext()){
            periodDescription = new PeriodDescription();
            periodDescription.setDay(c.getInt(c.getColumnIndex("day")));
            periodDescription.setTime(c.getString(c.getColumnIndex("time")));
            periodDescription.setType(c.getString(c.getColumnIndex("status")));
            periodDescriptions.add(periodDescription);
        }
        c.close();

        for(int i = 1 ; i < 8 ; i++){
            period = new Period();
            for(PeriodDescription pd : periodDescriptions){
                if(pd.getDay() == i){
                    if(pd.getType().contentEquals("open")){
                        period.setIsOpen(pd);
                    }else if(pd.getType().contentEquals("close")){
                        period.setIsClose(pd);
                    }
                }
            }
            periods.add(period);
        }
        return periods;
    }

    @Override
    public void bulkInsertPeriods(String placeId, List<Period> periods) {
        if(periods == null){
            return;
        }
        if (periods.size() > 0) {
            ContentValues[] values = new ContentValues[periods.size() * 2];
            ContentValues value;
            for (int i = 0, j = 0; i < periods.size(); i++) {
                value = null;
                Period p = periods.get(i);
                if (p.getIsClose() != null) {
                    value = new ContentValues();
                    value.put("place_id", placeId);
                    value.put("day", p.getIsClose().getDay());
                    value.put("time", p.getIsClose().getTime());
                    value.put("status", "close");
                }

                values[j++] = value;
                value = null;

                if (p.getIsOpen() != null) {
                    value = new ContentValues();
                    value.put("place_id", placeId);
                    value.put("day", p.getIsOpen().getDay());
                    value.put("time", p.getIsOpen().getTime());
                    value.put("status", "open");

                }
                values[j++] = value;
            }
            context.getContentResolver().bulkInsert(
                    AvBContract.PlacePeriodEntry.PLACE_PERIOD_URI, values);
        }
    }

    @Override
    public Period getTodayPeriod(String placeId) {

        Calendar c = Calendar.getInstance();
        List<Period> periodList = findPeriodsPlaceId(placeId);

        for(Period p : periodList) {
            if (p.getIsClose() != null && p.getIsOpen() != null) {
                if (p.getIsOpen().getDay() == c.get(Calendar.DAY_OF_WEEK) && p.getIsClose().getDay() == c.get(Calendar.DAY_OF_WEEK)) {
                    return p;
                }
            }
        }
        return null;
    }
}
