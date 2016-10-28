package org.avaliabrasil.avaliabrasil2.avb.dao;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilCategory;

import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public interface PlaceCategoryDAO {

    public void bulkInsertPlaceCategory(List<AvaliaBrasilCategory> categories);
}
