package org.avaliabrasil.avaliabrasil.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Pedro on 23/02/2016.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final String GOOGLE_PLACE = "asdasgd8218hdddDdsSAD";

    private static final Uri TEST_PLACE_DIR = AvBContract.PlaceEntry.CONTENT_URI;
    private static final Uri TEST_PLACE_ITEM = AvBContract.PlaceEntry.buildGooglePlaceUri(GOOGLE_PLACE);

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
//    public void testUriMatcher() {
//        UriMatcher testMatcher = WeatherProvider.buildUriMatcher();
//
//        assertEquals("Error: The WEATHER URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_DIR), WeatherProvider.WEATHER);
//        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), WeatherProvider.WEATHER_WITH_LOCATION);
//        assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);
//        assertEquals("Error: The LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_LOCATION_DIR), WeatherProvider.LOCATION);
//    }
}
