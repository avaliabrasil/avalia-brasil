package org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.object.ResultDetails;

/**
 * Created by Developer on 13/05/2016.
 */
public interface PlaceDetailsDAO {

    public ResultDetails getPlaceDetailsByPlaceId(String place_id);

}
