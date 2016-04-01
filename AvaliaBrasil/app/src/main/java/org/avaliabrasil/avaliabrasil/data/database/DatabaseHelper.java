package org.avaliabrasil.avaliabrasil.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.avaliabrasil.avaliabrasil.data.AvBContract;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static String database = "AvaliaBrasil";

    public static String place_table = "place";
    public static String place_detail_table = "place_detail";

    public static final int version = 2;

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
            "    distance  INTEGER," +
            "    latitude  DOUBLE DEFAULT (0) " +
            "                      NOT NULL," +
            "    longitude DOUBLE DEFAULT (0) " +
            "                      NOT NULL" +
            ");";

    private static final String createQueryInstrument = "CREATE TABLE "+ AvBContract.InstrumentEntry.TABLE_NAME +" (" +
            AvBContract.InstrumentEntry._ID + " INTEGER PRIMARY KEY," +
            AvBContract.InstrumentEntry.INSTRUMENT_ID +" TEXT UNIQUE NOT NULL," +
            AvBContract.InstrumentEntry.UPDATED_AT   +" TEXT NOT NULL" +
            ");";

    private static final String createQueryInstrumentPlaces = "CREATE TABLE instrument_places (" +
            "    _id           INTEGER PRIMARY KEY," +
            "    instrument_id INTEGER REFERENCES instrument (instrument_id) ON DELETE CASCADE" +
            "                                                                ON UPDATE CASCADE" +
            "                          NOT NULL," +
            "    place_id      TEXT    REFERENCES place (place_id) " +
            "                          NOT NULL" +
            ");";

    private static final String createQueryGroup_question = "CREATE TABLE group_question (" +
            "    _id            INTEGER PRIMARY KEY" +
            "                           NOT NULL," +
            "    instrument_id  INTEGER REFERENCES instrument (instrument_id) ON DELETE CASCADE" +
            "                                                                 ON UPDATE CASCADE," +
            "    group_id       INTEGER NOT NULL" +
            "                           UNIQUE," +
            "    order_question INTEGER NOT NULL" +
            ");";

    private static final String createQueryQuestion = "CREATE TABLE question (" +
            "    _id          INTEGER PRIMARY KEY" +
            "                         NOT NULL," +
            "    question_id  INTEGER NOT NULL," +
            "    question     TEXT    NOT NULL," +
            "    questionType TEXT    NOT NULL," +
            "    group_id     INTEGER REFERENCES group_question (group_id) ON DELETE CASCADE" +
            "                                                              ON UPDATE CASCADE" +
            ");";

    DatabaseHelper(Context context){
        super(context, database, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(createQueryPlace);
        db.execSQL(createQueryPlaceDetails);
        db.execSQL(createQueryInstrument);
        db.execSQL(createQueryInstrumentPlaces);
        db.execSQL(createQueryGroup_question);
        db.execSQL(createQueryQuestion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch(newVersion){
            case 2:
                db.execSQL("ALTER TABLE place ADD Column distance INTEGER");
        }
    }
}
