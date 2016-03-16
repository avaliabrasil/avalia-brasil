package org.avaliabrasil.avaliabrasil.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *
 * Utility class to acess AvaliaBrasil WebService, using <a href="http://developer.android.com/intl/pt-br/training/volley/index.html">Volley</a>
 * Using <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 *
 * @see <a href="http://developer.android.com/intl/pt-br/training/volley/index.html">Volley</a>
 * @see <a href="https://en.wikipedia.org/wiki/Representational_state_transfer">Restfull</a>.
 * @see <a href="http://www.tutorialspoint.com/design_pattern/factory_pattern.htm/">Factory Design Pattern</a>.
 * @since 1.0
 * @version 1.0
 */
public class AvaliaBrasilAPIClient {

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
    private static String avaliabrasilApiTarget = "http://api.avaliabrasil.org/";

    /**
     * Default path to the AvaliaBrasil API call to get user token.
     * Need to be called has POST method, and pass the user id in the body.
     * @since 1.0
     * @version 1.0
     * @return {@link String} targeting the base API.
     */
    public static String getUserToken(){
        StringBuilder target = new StringBuilder();
        target.append(avaliabrasilApiTarget);
        target.append("user/");
        target.append("authenticate");

        Log.d("GoogleAPI", "URL: " + target.toString());

        return target.toString();
    }
}
