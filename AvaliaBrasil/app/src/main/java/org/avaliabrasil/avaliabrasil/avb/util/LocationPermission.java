package org.avaliabrasil.avaliabrasil.avb.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import org.avaliabrasil.avaliabrasil.R;

/**
 * Created by Developer on 27/04/2016.
 */
public class LocationPermission {

    /**
     * TODO refatorar para classe única
     */
    public static final int INITIAL_REQUEST = 1337;
    public static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private Activity activity;

    public LocationPermission(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check the user permissions to make sure the app work property in the phone
     */
    public void checkForPermissions(Class activityWhereTo) {
        if (!canAccessCoarseLocation() || !canAccessFineLocation()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        } else {
            Intent intent = new Intent(activity, activityWhereTo);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Class activityWhereTo) {
        switch (requestCode) {
            case INITIAL_REQUEST:
                if (canAccessFineLocation() && canAccessCoarseLocation()) {
                    Intent intent = new Intent(activity, activityWhereTo);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("showSplash", false);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.location_access_needed), Toast.LENGTH_LONG).show();
                    activity.finish();
                }
                break;
        }
    }

    public boolean canAccessFineLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public boolean canAccessCoarseLocation() {
        return (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }


    public boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == activity.checkSelfPermission(perm));
        }
        return true;
    }
}
