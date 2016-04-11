package org.avaliabrasil.avaliabrasil.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.avaliabrasil.avaliabrasil.data.database.DatabaseHelper;
import org.avaliabrasil.avaliabrasil.data.database.DatabaseWrapper;

/**
 * Created by Pedro on 23/02/2016.
 */
public class AvBProvider extends ContentProvider {
    public final String LOG_TAG = this.getClass().getSimpleName();

    public static final String CONTENT_AUTHORITY = "org.avaliabrasil.avaliabrasil";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final Uri PLACE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY+"/places");

    public static final Uri PLACE_DETAILS_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY+"/placesdetails");

    static final int PLACE = 1;
    static final int PLACE_ID = 2;
    static final int PLACEDETAILS = 3;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, "places", PLACE);
        uriMatcher.addURI(CONTENT_AUTHORITY, "places/*", PLACE_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, "placesdetails", PLACEDETAILS);
        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_INSTRUMENT,AvBContract.INSTRUMENT);
        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_INSTRUMENTS,AvBContract.INSTRUMENTS);
        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_INSTRUMENT_PLACE,AvBContract.INSTRUMENT_PLACE);

        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_GROUP,AvBContract.GROUP_QUESTION);
        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_GROUPS,AvBContract.GROUP_QUESTIONS);

        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_QUESTION,AvBContract.QUESTION);
        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_QUESTIONS,AvBContract.QUESTIONS);

        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_SURVEY,AvBContract.SURVEY);

        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_NEW_PLACE,AvBContract.NEWPLACE);

        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_PLACE_CATEGORY,AvBContract.PLACE_CATEGORY);

        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_PLACE_TYPE,AvBContract.PLACE_TYPE);
        uriMatcher.addURI(CONTENT_AUTHORITY, AvBContract.PATH_PLACE_TYPES,AvBContract.PLACE_TYPES);
    }

    public static Uri getPlaceDetails(String place_id){
        Uri uri = Uri.parse("content://"+CONTENT_AUTHORITY+"/places/" + place_id);
        return uri;
    }

    public static Uri getInstruments(String place_id){
        Uri uri = Uri.parse(place_id);
        return uri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        return DatabaseWrapper.setUp(context);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor c;

        db = DatabaseWrapper.getDatabase(getContext());

        switch (uriMatcher.match(uri)) {
            case PLACE:
                qb.setTables(DatabaseHelper.place_table);

                if(selectionArgs != null){
                    c = qb.query(db,projection,	"name like ?", selectionArgs,null, null, "distance asc");
                }else{
                    c = qb.query(db,projection,	selection, selectionArgs,null, null, "distance asc");
                }

                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case PLACE_ID:
                c = db.rawQuery("select * from place_detail LEFT OUTER join place on place_detail.place_id = place.place_id where place_detail.place_id = ? order by distance asc",new String[]{ uri.getPathSegments().get(1)});
                break;
           case AvBContract.INSTRUMENTS:

                //TODO fix this call.
                c =  db.rawQuery("select instrument.instrument_id,instrument.updated_at from instrument_places left join instrument on instrument.instrument_id = instrument_places.instrument_id where place_id = ? ",new String[]{ uri.getPathSegments().get(1)});
                break;
            case AvBContract.GROUP_QUESTIONS:
                /*c =  db.rawQuery("SELECT *" +
                        "  FROM group_question left join question on question.group_id = group_question.group_id" +
                        " WHERE group_question.instrument_id = ? ",new String[]{ uri.getPathSegments().get(1)});*/

                c = db.rawQuery("select * from instrument left join group_question on instrument.instrument_id = group_question.instrument_id left join question on question.group_id = group_question.group_id " +
                        "where instrument.instrument_id = ? ",new String[]{ uri.getPathSegments().get(1)});

                break;
            case AvBContract.SURVEY:

                c = db.query(AvBContract.SurveyEntry.TABLE_NAME,projection, selection, selectionArgs,null,null,sortOrder);
                break;

            case AvBContract.NEWPLACE:
                c = db.rawQuery("select * from newPlace",null);
                break;

            case AvBContract.PLACE_CATEGORY:
                c = db.rawQuery("select * from place_category",null);
                break;
            case AvBContract.PLACE_TYPES:
                c = db.rawQuery("select * from place_type where category_id = ? ",new String[]{ uri.getPathSegments().get(1)});
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case PLACE:
                return "vnd.android.cursor.dir/vnd."+ CONTENT_AUTHORITY + DatabaseHelper.place_table  +" ";
            case PLACE_ID:
                return "vnd.android.cursor.item/vnd."+ CONTENT_AUTHORITY + DatabaseHelper.place_detail_table  +" ";
            case AvBContract.INSTRUMENT:
                return AvBContract.InstrumentEntry.CONTENT_TYPE;
            case AvBContract.INSTRUMENTS:
                return AvBContract.InstrumentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = null;
        long rowID;

        switch (uriMatcher.match(uri)){
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict(	DatabaseHelper.place_table, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if(rowID == -1){
                    db.update(DatabaseHelper.place_table,values,"where place_id = ?",new String[]{values.getAsString("place_id")});
                }
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(BASE_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
            case PLACEDETAILS:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict(	DatabaseHelper.place_detail_table, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if(rowID == -1){
                    db.update(DatabaseHelper.place_detail_table,values,"where place_id = ?",new String[]{values.getAsString("place_id")});
                }
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(BASE_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.INSTRUMENT:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.InstrumentEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.InstrumentEntry.INSTRUMENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.INSTRUMENT_PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.InstrumentPlaceEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.InstrumentEntry.INSTRUMENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.GROUP_QUESTION:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.GroupQuestionEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.GroupQuestionEntry.GROUP_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.QUESTION:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.QuestionEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.QuestionEntry.QUESTION_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.SURVEY:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.SurveyEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.SurveyEntry.SURVEY_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.NEWPLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.NewPlaceEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.NewPlaceEntry.NEWPLACE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
            case AvBContract.PLACE_CATEGORY:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.PlaceCategoryEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.NewPlaceEntry.NEWPLACE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.PLACE_TYPE:
                db = DatabaseWrapper.getDatabase(getContext());
                rowID = db.insertWithOnConflict( AvBContract.PlaceTypeEntry.TABLE_NAME, "", values,SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0)
                {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.NewPlaceEntry.NEWPLACE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = null;

        switch (uriMatcher.match(uri)){
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                count = db.delete(DatabaseHelper.place_table, selection, selectionArgs);
                break;
            case PLACE_ID:
                db = DatabaseWrapper.getDatabase(getContext());
                String id = uri.getPathSegments().get(1);
                count = db.delete( DatabaseHelper.place_table, "_id" +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case AvBContract.SURVEY:
                db = DatabaseWrapper.getDatabase(getContext());
                count = db.delete(AvBContract.SurveyEntry.TABLE_NAME, selection,selectionArgs);
                break;
            case AvBContract.NEWPLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                count = db.delete(AvBContract.NewPlaceEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = null;

        switch (uriMatcher.match(uri)){
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                count = db.update( DatabaseHelper.place_table, values, selection, selectionArgs);
                break;
            case PLACE_ID:
                db = DatabaseWrapper.getDatabase(getContext());

                count = db.update( DatabaseHelper.place_table, values, "_id = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            case AvBContract.GROUP_QUESTION:
                db = DatabaseWrapper.getDatabase(getContext());
                count = db.update( AvBContract.GroupQuestionEntry.TABLE_NAME, values,selection,selectionArgs);
               break;
            case AvBContract.SURVEY:
                db = DatabaseWrapper.getDatabase(getContext());
                count = db.update( AvBContract.SurveyEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = null;
        int returnCount = 0;

        switch (uriMatcher.match(uri)) {
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(	DatabaseHelper.place_table, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if(rowID == -1){
                            db.update(DatabaseHelper.place_table,value,"where place_id = ?",new String[]{value.getAsString("place_id")});
                        }
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case AvBContract.INSTRUMENT:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if(value == null){
                            continue;
                        }
                        long rowID = db.insertWithOnConflict(AvBContract.InstrumentEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case AvBContract.INSTRUMENT_PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if(value == null){
                            continue;
                        }
                        long rowID = db.insertWithOnConflict(AvBContract.InstrumentPlaceEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case AvBContract.GROUP_QUESTION:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.GroupQuestionEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case AvBContract.QUESTION:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.QuestionEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;


            case AvBContract.SURVEY:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.SurveyEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case AvBContract.NEWPLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.NewPlaceEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case AvBContract.PLACE_CATEGORY:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.PlaceCategoryEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case AvBContract.PLACE_TYPE:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.PlaceTypeEntry.TABLE_NAME, "", value,SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
