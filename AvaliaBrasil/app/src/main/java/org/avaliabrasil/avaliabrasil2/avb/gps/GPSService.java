package org.avaliabrasil.avaliabrasil2.avb.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil2.avb.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;

/**
 * Created by Developer on 17/05/2016.
 */
public class GPSService extends Service implements LocationListener , SharedPreferences.OnSharedPreferenceChangeListener{

    private String TAG = this.getClass().getSimpleName();

    private AvaliaBrasilApplication avaliaBrasilApplication;

    /**
     * Contant that define 1 hour.
     */
    private static int minTimeBetweenUpdates = 3600 * 1000;

    /**
     * Contant that define 5km.
     */
    private static int minDistanceBetweenUpdates = 5 * 1000;

    /**
     * The {@link LocationManager} for get the user place/location or, if there is not, search for it.
     */
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Starting GPSService");
        avaliaBrasilApplication = (AvaliaBrasilApplication)getApplication();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            if (!locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            } else {
                Location providerLocation;
                for (String provider : locationManager.getAllProviders()) {

                    providerLocation = locationManager.getLastKnownLocation(provider);

                    if (providerLocation != null) {
                        if (Utils.isBetterLocation(providerLocation, avaliaBrasilApplication.getLocation())) {
                            avaliaBrasilApplication.setLocation(providerLocation);
                            GooglePlacesAPIClient.getNearlyPlaces(GPSService.this, providerLocation);
                        }
                    }
                }
                addRequestLocationUpdates();
             }
        }catch (SecurityException e) {
            Log.e(TAG, "onCreate: ",e);
            e.printStackTrace();
        }


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GPSService.this);
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Destroying GPSService");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GPSService.this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location providerLocation) {
        Log.d(TAG, "onLocationChanged: Locate has changed.");
        if (providerLocation != null) {
            if (Utils.isBetterLocation(providerLocation, avaliaBrasilApplication.getLocation())) {
                avaliaBrasilApplication.setLocation(providerLocation);
                GooglePlacesAPIClient.getNearlyPlaces(GPSService.this, providerLocation);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.pref_gps_status_key)) ) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GPSService.this);
            try{
                if(sp.getBoolean(key,false)){
                    Log.d(TAG, "onSharedPreferenceChanged: addRequestLocationUpdates");
                    removeAllUpdates();
                    addRequestLocationUpdates();
                }else{
                    Log.d(TAG, "onSharedPreferenceChanged: removeAllUpdates");
                    removeAllUpdates();
                }
            }catch(SecurityException e){
                Log.e(TAG, "onSharedPreferenceChanged: ",e);
                e.printStackTrace();
            }
        }
    }

    private void removeAllUpdates() throws SecurityException{
        locationManager.removeUpdates(GPSService.this);
    }

    private void addRequestLocationUpdates() throws SecurityException{
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeBetweenUpdates, minDistanceBetweenUpdates, GPSService.this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeBetweenUpdates, minDistanceBetweenUpdates, GPSService.this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, minTimeBetweenUpdates, minDistanceBetweenUpdates, GPSService.this);
    }
}
