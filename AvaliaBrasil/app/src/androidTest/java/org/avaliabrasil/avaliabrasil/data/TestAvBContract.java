package org.avaliabrasil.avaliabrasil.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Pedro on 23/02/2016.
 */
public class TestAvBContract extends AndroidTestCase {

    private static final String GOOGLE_PLACE = "asdasgd8218hdddDdsSAD";

//     public void testBuild

    public void testBuildGooglePlaceUri(){
        Uri placeUri = AvBContract.PlaceEntry.buildGooglePlaceUri(GOOGLE_PLACE);

        assertNotNull("Null Uri returned", placeUri);

        assertEquals("Google Place Id wasn't appended to the end of Uri", GOOGLE_PLACE,
                placeUri.getLastPathSegment());

        assertEquals("Google Place Uri doesn't match our expected result", placeUri.toString(),
                "content://org.avaliabrasil.avaliabrasil/place/asdasgd8218hdddDdsSAD");
    }
}
