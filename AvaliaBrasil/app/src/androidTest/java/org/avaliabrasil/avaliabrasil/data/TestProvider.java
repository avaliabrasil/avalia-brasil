package org.avaliabrasil.avaliabrasil.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.avaliabrasil.avaliabrasil.data.AvBContract.PlaceEntry;

import java.util.UUID;

/**
 * Created by Pedro on 23/02/2016.
 */
public class TestProvider extends AndroidTestCase {

    /*public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
   /* public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PlaceEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
   /* public void deleteAllRecordsFromDB() {
        AvBDBHelper dbHelper = new AvBDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(PlaceEntry.TABLE_NAME, null, null);
        db.close();
    }

    /* TODO: Refactor here!
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
   /* public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */

   /* public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                AvBProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: AvbProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + AvBContract.CONTENT_AUTHORITY,
                    providerInfo.authority, AvBContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */

    /*public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(PlaceEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the PlaceEntry CONTENT_URI should return PlaceEntry.CONTENT_TYPE",
                PlaceEntry.CONTENT_TYPE, type);

        String testGooglePlaceId = "asdad1wdasda";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                PlaceEntry.buildGooglePlaceUri(testGooglePlaceId));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the PlaceEntry CONTENT_URI with GooglePlaceId should return PlaceEntry.CONTENT_ITEM_TYPE",
                PlaceEntry.CONTENT_ITEM_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
  /*  public void testPlaceQuery() {
        // insert our test records into the database
        AvBDBHelper dbHelper = new AvBDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createPlaceValues();
        long placeRowId = TestUtilities.insertPlaceValues(mContext);

        db.close();

        // Test the Place Query
        Cursor placeCursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testPlaceQuery", placeCursor, testValues);
    }


    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    /*public void testUpdatePlace() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createPlaceValues();

        Uri placeUri = mContext.getContentResolver().
                insert(PlaceEntry.CONTENT_URI, values);

        String googlePlaceId;
        googlePlaceId = placeUri.getLastPathSegment();

        // Verificando se retornamos um id de google Place
        assertFalse("Essa string tem alguma coisa?", googlePlaceId.isEmpty());

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(PlaceEntry.COLUMN_PLACE_ID, googlePlaceId);
        updatedValues.put(PlaceEntry.COLUMN_NAME, "New Test Place Name");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor placeCursor = mContext.getContentResolver().query(PlaceEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        placeCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                PlaceEntry.CONTENT_URI, updatedValues, PlaceEntry.COLUMN_PLACE_ID + "= ?",
                new String[] {googlePlaceId});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        placeCursor.unregisterContentObserver(tco);
        placeCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null,   // projection
                PlaceEntry.COLUMN_PLACE_ID + " = " + googlePlaceId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createPlaceValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PlaceEntry.CONTENT_URI, true, tco);

        // Aqui a Uri era setada para um Id numérico, no meu caso, isso é diferente!
        Uri placeUri = mContext.getContentResolver().insert(PlaceEntry.CONTENT_URI, testValues);

        // Parar aqui por enquanto!


        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        String googlePlaceId;
        googlePlaceId = placeUri.getLastPathSegment();

        // Verificando se retornamos um id de google Place
        assertFalse("Essa string tem alguma coisa?", googlePlaceId.isEmpty());

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating PlaceEntry.",
                cursor, testValues);
    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for Place Delete
        TestUtilities.TestContentObserver placeObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PlaceEntry.CONTENT_URI, true, placeObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        placeObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(placeObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertPlaceValues() {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            TestUtilities testUtilities = new TestUtilities();
            ContentValues placeValues =  testUtilities.createPlaceValues();
            String randomId = UUID.randomUUID().toString();
            placeValues.put(PlaceEntry.COLUMN_PLACE_ID, randomId);
            returnContentValues[i] = placeValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {
        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertPlaceValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver placeObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PlaceEntry.CONTENT_URI, true, placeObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(PlaceEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        placeObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(placeObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }*/
}

