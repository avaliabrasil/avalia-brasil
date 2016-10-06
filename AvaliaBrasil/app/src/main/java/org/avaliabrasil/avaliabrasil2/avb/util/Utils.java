package org.avaliabrasil.avaliabrasil2.avb.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.FileInputStream;
import java.util.HashMap;

/**
 * Created by Developer on 12/04/2016.
 */
public abstract class Utils {

    /**
     * Show the user a message if the GPS is turned off.
     */
    public static void showGPSDisabledAlertToUser(final AppCompatActivity context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(
                        "GPS está desativado em seu dispositivo. Deseja ativa-lo?")
                .setCancelable(false)
                .setPositiveButton("Configurações de ativação do GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Constant for 2 minutes.
     */
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Check if the last user place is better than the last one.
     *
     * @param location
     * @param currentBestLocation
     * @return if the new location is better than the older.
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    public static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static Bitmap getImageBitmap(Context context) {
        try {
            FileInputStream fis = context.openFileInput("profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeProfilePhoto(Context context) {
        try {
            context.deleteFile("profile.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String normalizeAvaliaBrasilResponse(String response) {
        response = response.replaceFirst("\\[", "");
        int ind = response.lastIndexOf("]");

        if (ind >= 0)
            response = new StringBuilder(response).replace(ind, ind + 1, "").toString();

        return response;
    }

    private static HashMap<String, String> states = new HashMap<>();

    public static String getStateAbbreviation(String state) {
        if (states.isEmpty()) {
            states.put("Acre", "AC");
            states.put("Alagoas", "AL");
            states.put("Amapá", "AP");
            states.put("Amazonas", "AM");
            states.put("Bahia", "BA");
            states.put("Ceará", "CE");
            states.put("Distrito Federal", "DF");
            states.put("Espírito Santo", "ES");
            states.put("Goiás", "GO");
            states.put("Maranhão", "MA");
            states.put("Mato Grosso", "MT");
            states.put("Mato Grosso do Sul", "MS");
            states.put("Minas Gerais", "MG");
            states.put("Pará", "PA");
            states.put("Paraíba", "PB");
            states.put("Paraná", "PR");
            states.put("Pernambuco", "PE");
            states.put("Piauí", "PI");
            states.put("Rio de Janeiro", "RJ");
            states.put("Rio Grande do Norte", "RN");
            states.put("Rio Grande do Sul", "RS");
            states.put("Rondônia", "RO");
            states.put("Roraima", "RR");
            states.put("Santa Catarina", "SC");
            states.put("São Paulo", "SP");
            states.put("Sergipe", "SE");
            states.put("Tocantins", "TO");
        }
        if (!states.containsKey(state)) {
            return state;
        }
        return states.get(state);
    }


    public static String formatTime(String time) {
        StringBuilder builder = new StringBuilder();
        builder.append(time.substring(0, 2));
        builder.append(":");
        builder.append(time.substring(2));
        return builder.toString();
    }
}
