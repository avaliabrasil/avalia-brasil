package org.avaliabrasil.avaliabrasil.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import org.avaliabrasil.avaliabrasil.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Pedro on 22/02/2016.
 */
public class TestUtilities extends AndroidTestCase {

    private static String GOOGLEPLACESID = "asdasgd8218hdddDdsSAD";

    static void validateCursor (String error, Cursor valueCursor, ContentValues expectedValues) {

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues){
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "'not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "1. "+ error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createPlaceValues () {
        ContentValues testValues = new ContentValues();
        testValues.put(AvBContract.PlaceEntry.COLUMN_PLACE_ID, GOOGLEPLACESID);
        testValues.put(AvBContract.PlaceEntry.COLUMN_NAME, "Google Place Teste!");
        testValues.put(AvBContract.PlaceEntry.COLUMN_DATE_TIME, "20150225 14:45");
        testValues.put(AvBContract.PlaceEntry.COLUMN_STATUS, "1");

        testValues.put(AvBContract.PlaceEntry.COLUMN_ADRESS, "Rua João Neves");
        testValues.put(AvBContract.PlaceEntry.COLUMN_PHONE, "51 4321-0293");
        testValues.put(AvBContract.PlaceEntry.COLUMN_EMAIL, "e-mail@site.com.br");
        testValues.put(AvBContract.PlaceEntry.COLUMN_OPEN_HOURS, "08:00 - 17:00");
        return testValues;
    }

    static long insertPlaceValues(Context context) {
        AvBDBHelper dbHelper = new AvBDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createPlaceValues();

        long rowId;
        rowId = db.insert(AvBContract.PlaceEntry.TABLE_NAME,null,testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Place Values", rowId != -1);

        return rowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
