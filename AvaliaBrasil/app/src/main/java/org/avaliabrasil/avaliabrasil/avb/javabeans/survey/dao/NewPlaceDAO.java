package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.NewPlace;

/**
 * Created by Developer on 12/05/2016.
 */
public interface NewPlaceDAO {

    public void insertNewPlace(NewPlace newPlace);
    public void deleteNewPlaceByPlaceId(String placeId);
    public NewPlace findNewPlaceByPlaceId(String placeId);
}
