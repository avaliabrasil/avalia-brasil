package org.avaliabrasil.avaliabrasil.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.data.AvBContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Pedro on 29/02/2016.
 */
public class AvbSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = this.getClass().getSimpleName();
    public static final String STRING_QUERY = "query";

    public AvbSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        // TODO  Deletar
        // String locationQuery = Utility.getPreferredLocation(getContext());

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Definindo uma Query:
        String query = "";
        boolean hasQuery = false;
        if (extras.getString(STRING_QUERY) != null) {
            query = extras.getString("query");
            hasQuery = true;
        }

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            final String API_BASE_URL = getContext().getString(R.string.api_url);
            final String PLACE_PATH = "/places/";
            final String REQUEST_URL = API_BASE_URL + PLACE_PATH + query;

            // TODO: Atualizar com o nome do lugar
            // String nameSubstring = "Ab"

            Uri builtUri = Uri.parse(REQUEST_URL).buildUpon()

                    // TODO: Habilitar quando implementar busca por nome
                    // .appendQueryParameter(NAME_PARAM, nameSubstring)

                    // TODO: Habilitar quando eu tiver uma chave para  API, se necessário
                    //.appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)

                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");


            // TODO: Autenticação:
//            final String USER_TOKEN = "userToken";
//            String userToken = pegar o token de algum lugar;
//            urlConnection.setRequestProperty(USER_TOKEN, userToken);

            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            String placesJsonStr = buffer.toString();
            insertPlacesFromJson(placesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void insertPlacesFromJson(String placesJsonStr)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Location information

        final String GGL_PLACE = "place";

        final String GGL_PLACE_ID = "googleId";
        final String GGL_NAME = "name";

        // Colunas que não estarão no Mysql, e virão do Google:
        final String GGL_ADRESS = "address";
        final String GGL_PHONE = "phone";
        // TODO : Mudar para ser email depois:
        final String GGL_EMAIL = "state";
        final String GGL_OPEN_HOURS = "openHours";

        try {
            JSONObject placesJson = new JSONObject(placesJsonStr);

            JSONArray placesArray = placesJson.getJSONArray(GGL_PLACE);

            Vector<ContentValues> placesVector = new Vector<ContentValues>(placesArray.length());

            for(int i = 0; i < placesArray.length(); i++) {
                // These are the values that will be collected.

                // Get the JSON object representing the day
                JSONObject place = placesArray.getJSONObject(i);

                String place_id = place.getString(GGL_PLACE_ID);
                String name = place.getString(GGL_NAME);
                String address = place.getString(GGL_ADRESS);
                String phone = place.getString(GGL_PHONE);
                String email = place.getString(GGL_EMAIL);
                String openhours = place.getString(GGL_OPEN_HOURS);

                ContentValues placeValues = new ContentValues();

                placeValues.put(AvBContract.PlaceEntry.COLUMN_PLACE_ID, place_id);
                placeValues.put(AvBContract.PlaceEntry.COLUMN_NAME, name);
                placeValues.put(AvBContract.PlaceEntry.COLUMN_ADRESS, address);
                placeValues.put(AvBContract.PlaceEntry.COLUMN_PHONE, phone);
                placeValues.put(AvBContract.PlaceEntry.COLUMN_EMAIL, email);
                placeValues.put(AvBContract.PlaceEntry.COLUMN_OPEN_HOURS, openhours);

                placesVector.add(placeValues);
            }

            // add to database
            if ( placesVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[placesVector.size()];
                placesVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(AvBContract.PlaceEntry.PLACES_URI, cvArray);
            }

            Log.d(LOG_TAG, "Place Insert Complete " + placesVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat the latitude of the city
     * @param lon the longitude of the city
     * @return the row ID of the added location.
     */
//    long addLocation(String locationSetting, String cityName, double lat, double lon) {
//        long locationId;
//
//        // First, check if the location with this city name exists in the db
//        Cursor locationCursor = getContext().getContentResolver().query(
//                WeatherContract.LocationEntry.CONTENT_URI,
//                new String[]{WeatherContract.LocationEntry._ID},
//                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
//                new String[]{locationSetting},
//                null);
//
//        if (locationCursor.moveToFirst()) {
//            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
//            locationId = locationCursor.getLong(locationIdIndex);
//        } else {
//            // Now that the content provider is set up, inserting rows of data is pretty simple.
//            // First create a ContentValues object to hold the data you want to insert.
//            ContentValues locationValues = new ContentValues();
//
//            // Then add the data, along with the corresponding name of the data type,
//            // so the content provider knows what kind of value is being inserted.
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);
//
//            // Finally, insert location data into the database.
//            Uri insertedUri = getContext().getContentResolver().insert(
//                    WeatherContract.LocationEntry.CONTENT_URI,
//                    locationValues
//            );
//
//            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
//            locationId = ContentUris.parseId(insertedUri);
//        }
//
//        locationCursor.close();
//        // Wait, that worked?  Yes!
//        return locationId;
//    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
//    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
//        Account account = getSyncAccount(context);
//        String authority = context.getString(R.string.content_authority);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // we can enable inexact timers in our periodic sync
//            SyncRequest request = new SyncRequest.Builder().
//                    syncPeriodic(syncInterval, flexTime).
//                    setSyncAdapter(account, authority).
//                    setExtras(new Bundle()).build();
//            ContentResolver.requestSync(request);
//        } else {
//            ContentResolver.addPeriodicSync(account,
//                    authority, new Bundle(), syncInterval);
//        }
//    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context, String query) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(STRING_QUERY, query);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            // Tirei daqui
            // onAccountCreated(newAccount, context);

            onAccountCreated(newAccount, context);

        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        // Não vamos configurar sincronização agendada:

//        /*
//         * Since we've created an account
//         */
//        AvbSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
//
//        /*
//         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
//         */
//        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        //Não vou chamar esta função agora!
        // syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
