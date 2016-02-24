package org.avaliabrasil.avaliabrasil.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Pedro on 23/02/2016.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final String GOOGLE_PLACE = "asdasgd8218hdddDdsSAD";

    // Como Formas as Uris!
    private static final Uri TEST_PLACE_DIR = AvBContract.PlaceEntry.CONTENT_URI;
    private static final Uri TEST_PLACE_ITEM = AvBContract.PlaceEntry.buildGooglePlaceUri(GOOGLE_PLACE);

    public void testUriMatcher() {
        UriMatcher testMatcher = AvBProvider.buildUriMatcher();

        assertEquals("Error: The URI was matched incorrectly.",
                testMatcher.match(TEST_PLACE_DIR),AvBProvider.PLACES_LIST);

        assertEquals("Error: The URI was matched incorrectly.",
                testMatcher.match(TEST_PLACE_ITEM),AvBProvider.PLACE);

    }
}
