package org.avaliabrasil.avaliabrasil.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceDetails;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *
 * Utility class to acess <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>, using from <a href="https://jersey.java.net/">Jersey</a>
 * Using <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 *
 * @see <a href="https://jersey.java.net/">Jersey</a>.
 * @see <a href="https://jersey.java.net/documentation/latest/client.html/">Jersey Client</a>.
 * @see <a href="https://en.wikipedia.org/wiki/Representational_state_transfer">Restfull</a>.
 * @see <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
 * @since 1.0
 * @version 1.0
 */
public class GooglePlacesAPIClient {

    /**
     * Since {@link Client} is a heavy object, we instantiate it single time, and acess his usefull methods.
     *
     * @see <a href="https://jersey.java.net/documentation/latest/client.html/">Jersey Client</a>.
     * @since 1.0
     * @version 1.0
     */
   // private static Client client = ClientBuilder.newClient();

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
     * Default path to the Google Places API calls.
     *
     * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
     * @since 1.0
     * @version 1.0
     * @return {@link WebTarget} targeting the base API.
     */
    //private static WebTarget getInstance() {
   //     return client.target(googleMapsApiTarget);
   // }

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
    public static String getNearlyPlaces(@NonNull double latitude ,@NonNull double longitude,@NonNull double radius,@Nullable String[] types ,@NonNull String key){
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

        if(types != null && types.length > 0){
            //target = target.queryParam("types",types);
        }

        target.append("&key=");
        target.append(key);

        Log.d("GoogleAPI", "URL: " + target.toString());

        return target.toString();
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
        //WebTarget target = getInstance();
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
