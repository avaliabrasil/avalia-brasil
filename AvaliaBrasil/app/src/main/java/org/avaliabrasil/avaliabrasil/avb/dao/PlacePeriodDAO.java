package org.avaliabrasil.avaliabrasil.avb.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.google.places.Period;

import java.util.List;

/**
 * Created by Developer on 19/05/2016.
 */
public interface PlacePeriodDAO {

    public List<Period> findPeriodsPlaceId(String placeId);
    public void bulkInsertPeriods(String placeId,List<Period> periods);
    public Period getTodayPeriod(String placeId);
}
