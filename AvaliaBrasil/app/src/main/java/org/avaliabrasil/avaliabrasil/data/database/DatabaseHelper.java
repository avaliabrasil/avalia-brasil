package org.avaliabrasil.avaliabrasil.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static String database = "AvaliaBrasil";

    public static String place_table = "place";
    public static String place_detail_table = "place_detail";

    public static final int version = 1;

    private static final String createQueryPlaceDetails = "CREATE TABLE place_detail (" +
            "    place_id             TEXT REFERENCES place (place_id) UNIQUE," +
            "    website              TEXT DEFAULT ('Not Declared')," +
            "    formattedPhoneNumber TEXT DEFAULT ('Not Declared') " +
            ");";

    private static final String createQueryPlace = "CREATE TABLE place (" +
            "    _id       INTEGER PRIMARY KEY," +
            "    place_id  TEXT    UNIQUE" +
            "                      NOT NULL," +
            "    name      TEXT," +
            "    vicinity  TEXT," +
            "    latitude  DOUBLE DEFAULT (0) " +
            "                      NOT NULL," +
            "    longitude DOUBLE DEFAULT (0) " +
            "                      NOT NULL" +
            ");";

    DatabaseHelper(Context context){
        super(context, database, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(createQueryPlace);
        db.execSQL(createQueryPlaceDetails);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  "place_detail");
        db.execSQL("DROP TABLE IF EXISTS " +  "place");

        onCreate(db);
    }
}
