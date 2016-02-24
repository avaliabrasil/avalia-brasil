package org.avaliabrasil.avaliabrasil.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

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
}
