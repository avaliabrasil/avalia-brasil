package org.avaliabrasil.avaliabrasil.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Pedro on 22/02/2016.
 */
public class TestUtilities extends AndroidTestCase {

    private static String GOOGLEPLACESID = "asdasgd8218hdddDdsSAD";

    public ContentValues createPlaceValues () {
        ContentValues testValues = new ContentValues();
        testValues.put(AvBContract.PlaceEntry.COLUMN_PLACE_ID, GOOGLEPLACESID);
        testValues.put(AvBContract.PlaceEntry.COLUMN_ADRESS, "Rua João Neves");
        testValues.put(AvBContract.PlaceEntry.COLUMN_DATE_TIME, "20150225 14:45");
        testValues.put(AvBContract.PlaceEntry.COLUMN_STATUS, "up");
        testValues.put(AvBContract.PlaceEntry.COLUMN_PHONE, "51 4321-0293");
        testValues.put(AvBContract.PlaceEntry.COLUMN_OPEN_HOURS, "08:00 - 17:00");
        return testValues;
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
