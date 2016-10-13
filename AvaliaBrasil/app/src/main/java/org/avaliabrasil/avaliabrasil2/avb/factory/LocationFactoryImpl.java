package org.avaliabrasil.avaliabrasil2.avb.factory;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationCity;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationCountry;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationRegion;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationState;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.facebook.GraphRequest.TAG;

/**
 * @author <a href="https://github.com/Klauswk">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class LocationFactoryImpl implements LocationFactory<Cursor> {

    private Context context;

    public LocationFactoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public Location getLocationByType(Cursor c) {
        while(c.moveToNext()){
            switch(c.getString(c.getColumnIndex(AvBContract.LocationEntry.TYPE))){
                case "1":
                    return new LocationCountry(c);
                case "2":
                    return new LocationRegion(c);
                case "3":
                    return new LocationState(c);
                case "4":
                    return new LocationCity(c);
                default:
                    return null;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location getRegionByState(@NonNull String state){
        LocationRegion region = null;

        if(!state.isEmpty()){
            if(state.contains("SC") || state.contains("RS") || state.contains("PR")){
                region =(LocationRegion) getLocationByType(context.getContentResolver().query(AvBContract.LocationEntry.getById("4"), null, null, null, null));
            }
            else if(state.contains("ES") || state.contains("MG") || state.contains("SP")|| state.contains("RJ")){
                region =(LocationRegion) getLocationByType(context.getContentResolver().query(AvBContract.LocationEntry.getById("3"), null, null, null, null));
            }
            else if(state.contains("GO") || state.contains("MS") || state.contains("DF")|| state.contains("MT")){
                region =(LocationRegion) getLocationByType(context.getContentResolver().query(AvBContract.LocationEntry.getById("5"), null, null, null, null));
            }
            else if(state.contains("MA") || state.contains("PI") || state.contains("CE")|| state.contains("RN")
                    || state.contains("PB")|| state.contains("PE")|| state.contains("AL")|| state.contains("SE")
                    || state.contains("BA")){
                region =(LocationRegion) getLocationByType(context.getContentResolver().query(AvBContract.LocationEntry.getById("2"), null, null, null, null));
            }
            else if(state.contains("RR") || state.contains("RO") || state.contains("AC")|| state.contains("AP")
                    || state.contains("AM")|| state.contains("PA")|| state.contains("TO")|| state.contains("SE")){
                region =(LocationRegion) getLocationByType(context.getContentResolver().query(AvBContract.LocationEntry.getById("1"), null, null, null, null));
            }else{
                Log.e(TAG, "getRegionByState: Not found: " + state );
            }
        }

        return region;
    }
}
