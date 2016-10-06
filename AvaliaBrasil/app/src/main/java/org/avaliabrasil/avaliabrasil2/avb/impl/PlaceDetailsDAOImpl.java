package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlaceDetailsDAO;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.place.placedetail.ResultDetails;

/**
 * Created by Developer on 13/05/2016.
 */
public class PlaceDetailsDAOImpl implements PlaceDetailsDAO {

    private Context context;

    public PlaceDetailsDAOImpl(Context context) {
        this.context = context;
    }

    
    
    @Override
    @Nullable
    public ResultDetails getPlaceDetailsByPlaceId(String placeId) {
        ResultDetails resultDetails = null;

        Cursor c = context.getContentResolver().query(
                AvBContract.PlaceEntry.getPlaceDetails(placeId), null, null, null, null);

        if(c.moveToNext()){
            resultDetails = new ResultDetails();
            resultDetails.setPlaceId(placeId);
            resultDetails.setName(c.getString(c.getColumnIndex("name")));
            resultDetails.setWebsite(c.getString(c.getColumnIndex("website")));
            resultDetails.setFormattedPhoneNumber(c.getString(c.getColumnIndex("formattedPhoneNumber")));
            resultDetails.setVicinity(c.getString(c.getColumnIndex("vicinity")));
            resultDetails.setLatlng(new LatLng(c.getDouble(c.getColumnIndex("latitude")), c.getDouble(c.getColumnIndex("longitude"))));
            resultDetails.setCityName(c.getString(c.getColumnIndex(AvBContract.PlaceDetailsEntry.CITY)));
            resultDetails.setStateLetter(c.getString(c.getColumnIndex(AvBContract.PlaceDetailsEntry.STATE)));
        }
        c.close();

        return resultDetails;
    }

    @Override
    public String getNameByPlaceId(String placeId) {
        Cursor c = context.getContentResolver().query(AvBContract.PlaceEntry.getPlaceDetails(placeId), null, null, null, null);

        if(c.moveToNext()){
            String name = c.getString(c.getColumnIndex("name"));
            c.close();
            return name;
        }
        return "";
    }

    @Override
    public void insertOrUpdatePlaceDetails(String placeId,ResultDetails resultDetails, boolean insert) {
        ContentValues value = new ContentValues();
        value.put("place_id", placeId);
        value.put("website", resultDetails.getWebsite());
        value.put("formattedPhoneNumber", resultDetails.getFormattedPhoneNumber());
        value.put("photo_reference", resultDetails.getPhotos().size() > 0 ? resultDetails.getPhotos().get(0).getPhotoReference() : "");

        value.put("city", resultDetails.getCity());
        value.put("state", resultDetails.getState());
        value.put("country", resultDetails.getCountry());

        if (insert) {
            context.getContentResolver().insert(
                    AvBContract.PlaceDetailsEntry.PLACE_DETAILS_URI, value);
        } else {
            context.getContentResolver().update(
                    AvBContract.PlaceDetailsEntry.PLACE_DETAILS_URI, value, "place_id = ?", new String[]{placeId});
        }
    }

    @Override
    public void updateCityAndState(String placeId, String city, String state) {
        ContentValues value = new ContentValues();

        Log.d("DAO", "updateCityAndState: chamando...");
        Log.d("DAO", "updateCityAndState: placeId: " + placeId);
        Log.d("DAO", "updateCityAndState: city:" + city);
        Log.d("DAO", "updateCityAndState: state:" + state);

        value.put("city", city);
        value.put("state", state);

        context.getContentResolver().update(
                AvBContract.PlaceDetailsEntry.PLACE_DETAILS_URI, value, "place_id = ?", new String[]{placeId});
    }
}
