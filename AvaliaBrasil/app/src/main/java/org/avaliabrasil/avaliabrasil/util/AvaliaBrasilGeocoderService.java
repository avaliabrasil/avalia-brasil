package org.avaliabrasil.avaliabrasil.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import org.avaliabrasil.avaliabrasil.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 02/05/2016.
 */
public class AvaliaBrasilGeocoderService {
    /**
     *
     */
    private Geocoder geocoder;

    private List<Address> address = new ArrayList<>();

    private double latitude, longitude;

    private String countryName;

    private String locality;

    private String adminArea;

    public AvaliaBrasilGeocoderService(Context context, Geocoder geocoder, double latitude, double longitude) throws IllegalArgumentException {
        this.geocoder = geocoder;
        if (latitude == 0 || longitude == 0) {
            throw new IllegalArgumentException(context.getResources().getString(R.string.location_error));
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public AvaliaBrasilGeocoderService(Context context, Geocoder geocoder, Location location) throws IllegalArgumentException {
        this.geocoder = geocoder;
        if (location == null) {
            throw new IllegalArgumentException(context.getResources().getString(R.string.location_error));
        }
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public void fetchAddress() throws IOException {

        address.clear();
        address = geocoder.getFromLocation(latitude, longitude, 5);
        locality = address.get(0).getLocality();
        countryName = address.get(0).getCountryName();
        adminArea = address.get(0).getAdminArea();

    }

    public void fetchAddress(double latitude, double longitude) throws IOException {
        address.clear();
        address = geocoder.getFromLocation(latitude, longitude, 5);
        locality = address.get(0).getLocality();
        countryName = address.get(0).getCountryName();
        adminArea = address.get(0).getAdminArea();
        address.get(0).getCountryCode();
    }

    public Geocoder getGeocoder() {
        return geocoder;
    }

    public void setGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }
}
