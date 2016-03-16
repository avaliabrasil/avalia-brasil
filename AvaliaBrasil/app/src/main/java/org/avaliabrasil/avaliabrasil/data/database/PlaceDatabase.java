package org.avaliabrasil.avaliabrasil.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class PlaceDatabase extends SQLiteOpenHelper {

    public static final String databaseName = "place";
    public static final int version = 1;

    private static final String createQuery = "CREATE TABLE place (" +
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

    PlaceDatabase(Context context){
        super(context, databaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  databaseName);
        onCreate(db);
    }
}
