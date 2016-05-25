package org.avaliabrasil.avaliabrasil.avb.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.ResultDetails;

/**
 * Created by Developer on 13/05/2016.
 */
public interface PlaceDetailsDAO {

    public ResultDetails getPlaceDetailsByPlaceId(String place_id);

    public String getNameByPlaceId(String placeId);

    public void insertOrUpdatePlaceDetails(String placeId,ResultDetails resultDetails, boolean insert);

}
