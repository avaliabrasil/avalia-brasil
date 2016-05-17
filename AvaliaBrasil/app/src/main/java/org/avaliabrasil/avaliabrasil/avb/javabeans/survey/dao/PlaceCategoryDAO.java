package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.AvaliaBrasilCategory;

import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public interface PlaceCategoryDAO {

    public void bulkInsertPlaceCategory(List<AvaliaBrasilCategory> categories);
}
