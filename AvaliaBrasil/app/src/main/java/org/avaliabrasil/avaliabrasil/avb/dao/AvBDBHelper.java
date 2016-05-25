package org.avaliabrasil.avaliabrasil.avb.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class AvBDBHelper extends SQLiteOpenHelper {
    private static String database = "AvaliaBrasil";

    public static final int version = 9;

    private static final String createQueryPlaceDetails = "CREATE TABLE place_detail (" +
            "    place_id             TEXT REFERENCES place (place_id) UNIQUE," +
            "    website              TEXT DEFAULT ('Not Declared')," +
            "    formattedPhoneNumber TEXT DEFAULT ('Not Declared'), " +
            "    country TEXT, " +
            "    state TEXT, " +
            "    city TEXT, " +
            "    photo_reference TEXT" +
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

    private static final String createQueryInstrument = "CREATE TABLE " + AvBContract.InstrumentEntry.TABLE_NAME + " (" +
            AvBContract.InstrumentEntry._ID + " INTEGER PRIMARY KEY," +
            AvBContract.InstrumentEntry.INSTRUMENT_ID + " TEXT UNIQUE NOT NULL," +
            AvBContract.InstrumentEntry.UPDATED_AT + " TEXT NOT NULL" +
            ");";

    private static final String createQueryInstrumentPlaces = "CREATE TABLE " + AvBContract.InstrumentPlaceEntry.TABLE_NAME + " (" +
            "    instrument_id INTEGER REFERENCES instrument (instrument_id) ON DELETE CASCADE" +
            "                                                                ON UPDATE CASCADE" +
            "                          NOT NULL," +
            "    place_id      TEXT    REFERENCES place (place_id) " +
            "                          NOT NULL," +
            "                                    PRIMARY KEY (" +
            "                                   instrument_id," +
            "                                   place_id" +
            "                                    )" +
            ");";

    private static final String createQueryGroup_question = "CREATE TABLE group_question (" +
            "    _id            INTEGER PRIMARY KEY" +
            "                           NOT NULL," +
            "    instrument_id  INTEGER REFERENCES instrument (instrument_id) ON DELETE CASCADE" +
            "                                                                 ON UPDATE CASCADE" +
            "                                                                 ," +
            "    group_id       INTEGER NOT NULL" +
            "                           UNIQUE," +
            "    order_question INTEGER NOT NULL" +
            ");";

    private static final String createQueryQuestion = "CREATE TABLE question (" +
            "    _id          INTEGER PRIMARY KEY" +
            "                         NOT NULL," +
            "    question_id  INTEGER UNIQUE NOT NULL," +
            "    question     TEXT    NOT NULL," +
            "    questionType TEXT    NOT NULL," +
            "    group_id     INTEGER REFERENCES group_question (group_id) ON DELETE CASCADE" +
            "                                                              ON UPDATE CASCADE" +
            ");";

    private static final String createSurvey = " CREATE TABLE survey (" +
            "    _id             INTEGER PRIMARY KEY" +
            "                            NOT NULL," +
            "    instrument_id   INTEGER REFERENCES instrument (instrument_id)," +
            "    group_id        INTEGER REFERENCES group_question (group_id)," +
            "    question_id     INTEGER REFERENCES question (question_id)," +
            "    question_type   TEXT," +
            "    place_id        INTEGER REFERENCES place (place_id)," +
            "    anwser          TEXT    NOT NULL," +
            "    survey_finished BOOLEAN DEFAULT false" +
            ");";

    private static final String createNewPlace = " CREATE TABLE newPlace (" +
            "            _id           INTEGER PRIMARY KEY" +
            "            NOT NULL," +
            "            place_id      INTEGER REFERENCES place (place_id)," +
            "    category_id   INTEGER," +
            "    place_type_id INTEGER," +
            "    survey_id     INTEGER REFERENCES survey (_id) "+
            "    );";

    private static final String createPlaceCategory = "CREATE TABLE place_category (" +
            "    _id         INTEGER PRIMARY KEY" +
            "                        NOT NULL," +
            "    category_id INTEGER NOT NULL," +
            "    name        TEXT    NOT NULL" +
            ");";

    private static final String createPlaceType = "CREATE TABLE place_type (" +
            "    _id         INTEGER PRIMARY KEY" +
            "                        NOT NULL," +
            "    category_id INTEGER REFERENCES place_category (category_id) " +
            "                        NOT NULL," +
            "    name        TEXT    NOT NULL" +
            ");";


    private static final String createPlacePeriod = "CREATE TABLE place_period (" +
            "    _id      INTEGER PRIMARY KEY," +
            "    place_id INTEGER REFERENCES place (place_id) ON DELETE CASCADE" +
            "                                                 ON UPDATE CASCADE" +
            "                     NOT NULL," +
            "    day      TEXT    NOT NULL," +
            "    time     TEXT    NOT NULL," +
            "    status   TEXT    NOT NULL" +
            ");";
    
    private static final String createAnwser = "CREATE TABLE anwser (" +
            "    _id         INTEGER        PRIMARY KEY" +
            "                               NOT NULL," +
            "    question_id    INTEGER     NOT NULL," +
            "    survey_id      INTEGER     REFERENCES survey (_id)," +
            "    anwser      TEXT    NOT NULL" +
            ");";


    public AvBDBHelper(Context context) {
        super(context, database, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createQueryPlace);
        db.execSQL(createQueryPlaceDetails);
        db.execSQL(createQueryInstrument);
        db.execSQL(createQueryInstrumentPlaces);
        db.execSQL(createQueryGroup_question);
        db.execSQL(createQueryQuestion);
        db.execSQL(createSurvey);
        db.execSQL(createNewPlace);
        db.execSQL(createPlaceCategory);
        db.execSQL(createPlaceType);
        db.execSQL(createPlacePeriod);
        db.execSQL(createAnwser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2:
                db.execSQL("ALTER TABLE place ADD Column distance INTEGER");
            case 3:
                db.execSQL(createSurvey);
                db.execSQL(createNewPlace);
            case 4:
                db.execSQL(createPlaceCategory);
                db.execSQL(createPlaceType);
            case 5:
                db.execSQL("alter table place_detail add column photo_reference TEXT ");
            case 7:
                db.execSQL(createPlacePeriod);
            case 8:
                db.execSQL("alter table place_detail add column country TEXT");
                db.execSQL("alter table place_detail add column state TEXT ");
                db.execSQL("alter table place_detail add column city TEXT ");
            case 9:
                db.execSQL(createAnwser);
                db.execSQL("alter table place_detail add column survey_id INTEGER REFERENCES survey (_id)");

        }
    }

    public boolean clearAllData(SQLiteDatabase db) {
        try {
            db.delete(AvBContract.PlaceTypeEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.PlaceCategoryEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.NewPlaceEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.SurveyEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.QuestionEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.GroupQuestionEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.InstrumentPlaceEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.InstrumentEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.PlaceDetailsEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.PlaceEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.PlacePeriodEntry.TABLE_NAME, null, null);
            db.delete(AvBContract.AnwserEntry.TABLE_NAME, null, null);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
