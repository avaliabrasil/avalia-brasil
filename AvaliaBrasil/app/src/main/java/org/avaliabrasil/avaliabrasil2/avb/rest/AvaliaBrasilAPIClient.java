package org.avaliabrasil.avaliabrasil2.avb.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *         <p/>
 *         Utility class to acess AvaliaBrasil WebService, using <a href="http://developer.android.com/intl/pt-br/training/volley/index.html">Volley</a>
 *         Using <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 * @version 1.0
 * @see <a href="http://developer.android.com/intl/pt-br/training/volley/index.html">Volley</a>
 * @see <a href="https://en.wikipedia.org/wiki/Representational_state_transfer">Restfull</a>.
 * @see <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 * @since 1.0
 */
public class AvaliaBrasilAPIClient {

    /**
     * Static object to transform the {@link JsonObject} to a java class.
     *
     * @version 1.0
     * @see <a href="https://github.com/google/gson">Gson lib</a>.
     * @since 1.0
     */
    private static Gson gson = new Gson();

    /**
     * Default path to the Google Places API calls.
     *
     * @version 1.0
     * @see <a href="https://developers.google.com/places/web-service/">Google Places WebService </a>
     * @since 1.0
     */
    private static String avaliabrasilApiTarget = "http://api.avaliabrasil.org/";


    public static String USRID = "userId";

    private static String android_id;

    /**
     * Default path to the AvaliaBrasil API call to get user token.
     * Need to be called has POST method, and pass the user id in the body.
     *
     * @return {@link String} targeting the base API.
     * @version 1.0
     * @since 1.0
     */
    public static String getUserTokenURL() {
        StringBuilder target = new StringBuilder();
        target.append(avaliabrasilApiTarget);
        //target.append("user/");
        target.append("authenticate");

        Log.d("AvaliaBrasilAPI", "URL: " + target.toString());

        return target.toString();
    }

    /**
     * Default path to the AvaliaBrasil API call to get survey.
     * Need to be called has GET method, and pass the user token in the body.
     *
     * @return {@link String} targeting the base API.
     * @version 1.0
     * @since 1.0
     */
    public static String getSurveyURL(String place_id) {
        StringBuilder target = new StringBuilder();
        target.append(avaliabrasilApiTarget);
        target.append("survey/");
        target.append(place_id);

        Log.d("AvaliaBrasilAPI", "URL: " + target.toString());

        return target.toString();
    }

    /**
     * Default path to the AvaliaBrasil API call post the anwser.
     * Need to be called has POST method, and pass the user token and anwsers in the body.
     *
     * @return {@link String} targeting the base API.
     * @version 1.0
     * @since 1.0
     */
    public static String postAnwsers(String place_id) {
        StringBuilder target = new StringBuilder();
        target.append(avaliabrasilApiTarget);
        target.append("survey/");
        target.append(place_id);

        Log.d("AvaliaBrasilAPI", "URL: " + target.toString());

        return target.toString();
    }

    /**
     * Default path to the AvaliaBrasil API call to get place ranking.
     * Need to be called has GET method, and pass the user token and place id in the body.
     *
     * @return {@link String} targeting the base API.
     * @version 1.0
     * @since 1.0
     */
    public static String getPlaceStatistics(String place_id) {
        StringBuilder target = new StringBuilder();
        target.append(avaliabrasilApiTarget);
        target.append("ranking/");
        target.append(place_id);

        Log.d("AvaliaBrasilAPI", "URL: " + target.toString());

        return target.toString();
    }

    /**
     * Default path to the AvaliaBrasil API call to get place ranking.
     * Need to be called has GET method, and pass the user token and place id in the body.
     *
     * @return {@link String} targeting the base API.
     * @version 1.0
     * @since 1.0
     */
    public static String getPlacesRanking(HashMap<String, String> params) {
        StringBuilder target = new StringBuilder();
        target.append(avaliabrasilApiTarget);
        target.append("ranking");

        if (params != null) {
            if (!params.isEmpty()) {
                Set keys = params.keySet();
                target.append("?");
                for (Object key : keys) {
                    target.append(key);
                    target.append("=");
                    target.append(params.get(key));
                    target.append("&");
                }
                target.deleteCharAt(target.toString().length() -1);
            }
        }

        Log.d("AvaliaBrasilAPI", "URL: " + target.toString());

        return target.toString();
    }

    /**
     * Default path to the AvaliaBrasil API call to get place ranking.
     * Need to be called has GET method, and pass the user token and place id in the body.
     *
     * @return {@link String} targeting the base API.
     * @version 1.0
     * @since 1.0
     */
    public static String getLocations() {
        StringBuilder target = new StringBuilder();
        target.append(avaliabrasilApiTarget);
        target.append("locations");

        Log.d("AvaliaBrasilAPI", "URL: " + target.toString());

        return target.toString();
    }
}
