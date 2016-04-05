package org.avaliabrasil.avaliabrasil.rest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.data.AvBProvider;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceDetails;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *
 * Utility class to acess <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>, using from <a href="http://developer.android.com/intl/pt-br/training/volley/index.html">Volley</a>
 * Using <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 *
 * @see <a href="http://developer.android.com/intl/pt-br/training/volley/index.html">Volley</a>
 * @see <a href="https://jersey.java.net/documentation/latest/client.html/">Jersey Client</a>.
 * @see <a href="https://en.wikipedia.org/wiki/Representational_state_transfer">Restfull</a>.
 * @see <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
 * @since 1.0
 * @version 1.0
 */
public class GooglePlacesAPIClient {

    /**
     * Static object to transform the {@link JsonObject} to a java class.
     *
     * @see <a href="https://github.com/google/gson">Gson lib</a>.
     * @since 1.0
     * @version 1.0
     */
    private static Gson gson = new Gson();

    /**
     * Default path to the Google Places API calls.
     *
     * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
     * @since 1.0
     * @version 1.0
     */
    private static String googleMapsApiTarget = "https://maps.googleapis.com/maps/api/place/";

    /**
     * Default path to the Google Places API call to get nearly places around the user.
     *
     * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
     * @since 1.0
     * @version 1.0
     * @param latitude
     * @param longitude
     * @param radius
     * @param types
     * @param key
     * @return {@link String} targeting the base API.
     */
    private static String getNearlyPlacesURL(@NonNull double latitude ,@NonNull double longitude,@NonNull double radius,@Nullable String[] types ,@NonNull String key){
        StringBuilder target = new StringBuilder();
        target.append(googleMapsApiTarget);
        target.append("nearbysearch");
        target.append("/json");
        target.append("?location=");
        target.append(latitude);
        target.append(",");
        target.append(longitude);

        target.append("&radius=");
        target.append(radius);

        target.append("&types=");
        target.append("airport|" +
                "bus_station|" +
                "courthouse|" +
                "embassy|" +
                "fire_station|" +
                "hospital|" +
                "health|" +
                "library|" +
                "museum|" +
                "pharmacy|" +
                "police|" +
                "post_office|" +
                "school|" +
                "university|" +
                "subway_station|" +
                "train_station|" +
                "zoo");

        target.append("&key=");
        target.append(key);

        Log.d("GoogleAPI", "URL: " + target.toString());

        return target.toString();
    }

    /**
     * Default path to the Google Places API call to get places by their name.
     *
     * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
     * @since 1.0
     * @version 1.0
     * @param latitude
     * @param longitude
     * @param radius
     * @param types
     * @param key
     * @param name
     * @return {@link String} targeting the base API.
     */
    private static String getPlacesByNameURL(@NonNull double latitude , @NonNull double longitude, @NonNull
    double radius, @Nullable String[] types ,
                                             @NonNull String key, @NonNull String name){
        StringBuilder target = new StringBuilder();
        target.append(googleMapsApiTarget);
        target.append("nearbysearch");
        target.append("/json");
        target.append("?location=");
        target.append(latitude);
        target.append(",");
        target.append(longitude);

        target.append("&radius=");
        target.append(radius);

        target.append("&types=");
        target.append("airport|" +
                "bus_station|" +
                "courthouse|" +
                "embassy|" +
                "fire_station|" +
                "hospital|" +
                "health|" +
                "library|" +
                "museum|" +
                "pharmacy|" +
                "police|" +
                "post_office|" +
                "school|" +
                "university|" +
                "subway_station|" +
                "train_station|" +
                "zoo");

        target.append("&name=");
        target.append(name.trim());

        target.append("&key=");
        target.append(key);

        Log.d("GoogleAPI", "URL: " + target.toString());

        return target.toString();
    }

    /**
     * Used for fill the {@link android.content.ContentProvider} with the nearly places values.
     * @param context
     * @param location
     */
    public static void getNearlyPlaces(final Context context, final Location location){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getNearlyPlacesURL(location.getLatitude(), location.getLongitude(), 5000, null, "AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        PlaceSearch placeSearch = gson.fromJson(response, PlaceSearch.class);

                        ContentValues[] values = new ContentValues[placeSearch.getResults().size()];
                        Location customLocation = new Location("");
                        ContentValues value = null;

                        for (int i = 0; i < values.length; i++) {
                            value = new ContentValues();
                            value.put("place_id",placeSearch.getResults().get(i).getPlaceId());
                            value.put("name",placeSearch.getResults().get(i).getName());
                            value.put("vicinity",placeSearch.getResults().get(i).getVicinity());
                            value.put("latitude",placeSearch.getResults().get(i).getGeometry().getLocation().getLat());
                            value.put("longitude",placeSearch.getResults().get(i).getGeometry().getLocation().getLng());

                            customLocation.setLatitude(placeSearch.getResults().get(i).getGeometry().getLocation().getLat());
                            customLocation.setLongitude(placeSearch.getResults().get(i).getGeometry().getLocation().getLng());

                            value.put("distance",(int)(location.distanceTo(customLocation)));
                            values[i] = value;
                        }

                        context.getContentResolver().bulkInsert(
                                AvBProvider.PLACE_CONTENT_URI, values);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context,"Não foi possível acessar os servidor do google.",Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    /**
     * Used for fill the {@link android.content.ContentProvider} with the nearly places values.
     * @param activity
     * @param location
     * @param name
     */
    public static void getPlacesByName(final AppCompatActivity activity,
                                       final LoaderManager.LoaderCallbacks<Cursor> loader,final Location location,final String name){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getPlacesByNameURL(location == null ? 0 : location.getLatitude(), location== null ? 0 : location.getLongitude(), 50000, null, "AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0",name),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        PlaceSearch placeSearch = gson.fromJson(response, PlaceSearch.class);

                        ContentValues[] values = new ContentValues[placeSearch.getResults().size()];

                        ContentValues value = null;

                        Location customLocation = new Location("");

                        for (int i = 0; i < values.length; i++) {
                            value = new ContentValues();
                            value.put("place_id",placeSearch.getResults().get(i).getPlaceId());
                            value.put("name",placeSearch.getResults().get(i).getName());
                            value.put("vicinity",placeSearch.getResults().get(i).getVicinity());
                            value.put("latitude", placeSearch.getResults().get(i).getGeometry().getLocation().getLat());
                            value.put("longitude", placeSearch.getResults().get(i).getGeometry().getLocation().getLng());
                            customLocation.setLatitude(placeSearch.getResults().get(i).getGeometry().getLocation().getLat());
                            customLocation.setLongitude(placeSearch.getResults().get(i).getGeometry().getLocation().getLng());

                            value.put("distance",(int)(location.distanceTo(customLocation)));
                            values[i] = value;
                        }

                        activity.getContentResolver().bulkInsert(
                                AvBProvider.PLACE_CONTENT_URI, values);

                        Bundle bundle = new Bundle();
                        //TODO melhorar
                        bundle.putString("query","%"+name+"%");
                        activity.getSupportLoaderManager().restartLoader(0,bundle,loader);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Bundle bundle = new Bundle();
                //TODO melhorar
                bundle.putString("query","%"+name+"%");
                activity.getSupportLoaderManager().restartLoader(0,bundle,loader);

                Toast.makeText(activity,"Não foi possível acessar os servidor do google.",Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(activity).add(stringRequest);

    }

    /**
     * Default path to the Google Places API call to get place details by his placeId.
     *
     * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
     * @since 1.0
     * @version 1.0
     * @param placeId
     * @param key
     * @return {@link PlaceDetails} targeting the base API.
     */
    public static String getPlaceDetails(@NonNull String placeId,@NonNull String key){
        StringBuilder target = new StringBuilder();
        target.append(googleMapsApiTarget);

        target.append("details");
        target.append("/json");
        target.append("?placeid=");
        target.append(placeId);
        target.append("&key=");
        target.append(key);

        Log.d("GoogleAPI", "URL: " + target.toString());

        return target.toString();
    }
}
