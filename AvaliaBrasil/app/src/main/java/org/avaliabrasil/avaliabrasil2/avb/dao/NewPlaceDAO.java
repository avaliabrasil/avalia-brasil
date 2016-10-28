package org.avaliabrasil.avaliabrasil2.avb.dao;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.NewPlace;

/**
 * Created by Developer on 12/05/2016.
 */
public interface NewPlaceDAO {

    public void insertNewPlace(NewPlace newPlace);
    public void deleteNewPlaceByPlaceId(String placeId);
    public NewPlace findNewPlaceByPlaceId(String placeId);
}
