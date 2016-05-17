package org.avaliabrasil.avaliabrasil.avb.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import org.avaliabrasil.avaliabrasil.R;

/**
 * Created by Developer on 17/05/2016.
 *
 * Used to detect whatever the user has enable/disable the GPS, and inform to the {@link GPSService} by the method {@link SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged}
 */
public class GpsLocationReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Provider has changed.");

        if (intent.getAction().contentEquals("android.location.PROVIDERS_CHANGED")){
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor spe = sp.edit();

            if (!locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                Log.d(TAG, "onReceive: Provider has been disable.");

                spe.putBoolean(context.getString(R.string.pref_gps_status_key), false);
                spe.commit();

            } else {

                Log.d(TAG, "onReceive: Provider has been enable.");

                spe.putBoolean(context.getString(R.string.pref_gps_status_key), true);
                spe.commit();
            }
        }
    }
}
