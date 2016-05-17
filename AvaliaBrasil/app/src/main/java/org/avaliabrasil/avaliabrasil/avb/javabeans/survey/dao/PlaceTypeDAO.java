package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.AvaliaBrasilPlaceType;

import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public interface PlaceTypeDAO {

    public void bulkInsertPlaceType(List<AvaliaBrasilPlaceType> types);
}
