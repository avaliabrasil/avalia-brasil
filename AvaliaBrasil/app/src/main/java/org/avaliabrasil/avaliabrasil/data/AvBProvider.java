package org.avaliabrasil.avaliabrasil.data;


import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Pedro on 23/02/2016.
 */
public class AvBProvider extends ContentProvider {

    // URI Matcher:
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AvBDBHelper mOpenHelper;


    // URI Codes:
    static final int PLACES_LIST = 100;
    static final int PLACE = 200;

    private static final SQLiteQueryBuilder sPlaceInfoQueryBuilder;

    static {
        sPlaceInfoQueryBuilder = new SQLiteQueryBuilder();

        // É necessário definir as tabelas de uma query builder antes de chamar a query.
        // Se for realizar um join, abaixo deve ser formada a string correspondente!
        // FROM ... place
        sPlaceInfoQueryBuilder.setTables(
                AvBContract.PlaceEntry.TABLE_NAME
        );
    }

    //place.place_id = ?
    private static final String sPlaceIdSelection =
            AvBContract.PlaceEntry.TABLE_NAME +
                    "." + AvBContract.PlaceEntry.COLUMN_PLACE_ID + " = ? ";


    private Cursor getPlaceByGooglePlacesId(Uri uri, String[] projection, String sortOrder) {
        String placeId = AvBContract.PlaceEntry.getPlaceIdFromUri(uri);
        String[] selectionArgs = new String[]{placeId} ;
        String selection = sPlaceIdSelection;

        return sPlaceInfoQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getAllPlaces(Uri uri, String[] projection, String sortOrder) {

        return sPlaceInfoQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }


    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AvBContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.

        matcher.addURI(authority, AvBContract.PATH_PLACE, PLACES_LIST);

        matcher.addURI(authority, AvBContract.PATH_PLACE + "/*",PLACE);

        // 3) Return the new matcher!
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new AvBDBHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override

    // Aqui estamos retornando o MIME TYPE da URI! Não sei muito bem o que isso significa.
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case PLACES_LIST:
                return AvBContract.PlaceEntry.CONTENT_TYPE;
            case PLACE:
                return AvBContract.PlaceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri on getType Content Provider Method " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Dependendo da Query realizada, este switch define que cursor deve ser retornado!
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // Lista de Lugares
            case PLACES_LIST:
            {
                retCursor = getAllPlaces(uri, projection, sortOrder);
                break;
            }
            // Informações de um Lugar
            case PLACE: {
                retCursor = getPlaceByGooglePlacesId(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown Uri on Query Content Provider Method" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLACE: {
                long _id = db.insert(AvBContract.PlaceEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    String place_id = values.getAsString(AvBContract.PlaceEntry.COLUMN_PLACE_ID);
                    returnUri = AvBContract.PlaceEntry.buildGooglePlaceUri(place_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri on Insert Content Provider Method " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        //  this makes delete all rows return the number of rows deleted
        if (selection == null) selection = "1";

        switch (match){
            case PLACE:
                rowsDeleted = db.delete(
                        AvBContract.PlaceEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri on Delete Content Provider Method " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /* Acho que não vou precisar desse método por enquanto.
    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
        }
    }*/

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final  SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PLACE:
                rowsUpdated = db.update(AvBContract.PlaceEntry.TABLE_NAME, values,selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri on Update Content Provider Method " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(AvBContract.PlaceEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
