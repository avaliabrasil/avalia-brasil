package org.avaliabrasil.avaliabrasil2.avb.dao;

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

import java.util.Calendar;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by Pedro on 23/02/2016.
 */
public class AvBProvider extends ContentProvider {
    public final String LOG_TAG = this.getClass().getSimpleName();

    private AvBDBHelper helper;

    private Context context;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACE, AvBContract.PLACE);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACES, AvBContract.PLACE_ID);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACES_DETAILS, AvBContract.PLACE_DETAILS);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_INSTRUMENT, AvBContract.INSTRUMENT);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_INSTRUMENTS, AvBContract.INSTRUMENTS);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_INSTRUMENT_PLACE, AvBContract.INSTRUMENT_PLACE);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_GROUP, AvBContract.GROUP_QUESTION);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_GROUPS, AvBContract.GROUP_QUESTIONS);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_QUESTION, AvBContract.QUESTION);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_QUESTIONS, AvBContract.QUESTIONS);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_SURVEY, AvBContract.SURVEY);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_NEW_PLACE, AvBContract.NEWPLACE);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACE_CATEGORY, AvBContract.PLACE_CATEGORY);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACE_TYPE, AvBContract.PLACE_TYPE);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACE_TYPES, AvBContract.PLACE_TYPES);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACE_PERIOD, AvBContract.PLACE_PERIOD);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_PLACE_PERIODS, AvBContract.PLACE_PERIODS);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_ANWSER, AvBContract.ANWSER);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_ANWSERS, AvBContract.ANWSERS);

        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_LOCATION, AvBContract.LOCATION);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_LOCATIONS, AvBContract.LOCATIONS);
        uriMatcher.addURI(AvBContract.CONTENT_AUTHORITY, AvBContract.PATH_LOCATIONSBYID, AvBContract.LOCATIONSBYID);
    }

    @Override
    public boolean onCreate() {
        this.context = getContext();
        helper = new AvBDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

            SQLiteDatabase db = helper.getWritableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            Cursor c;

            switch (uriMatcher.match(uri)) {
                case AvBContract.PLACE:
                    qb.setTables(AvBContract.PlaceEntry.TABLE_NAME);

                    if (selectionArgs != null) {
                        c = qb.query(db, projection, "name like ?", selectionArgs, null, null, "distance asc");
                    } else {
                        c = qb.query(db, projection, selection, selectionArgs, null, null, "distance asc");
                    }

                    c.setNotificationUri(getContext().getContentResolver(), uri);
                    break;
                case AvBContract.PLACE_ID:
                    c = db.rawQuery("select * from place_detail LEFT OUTER join place on place_detail.PLACE_ID = place.PLACE_ID where place_detail.PLACE_ID = ? order by distance asc", new String[]{uri.getPathSegments().get(1)});
                    break;
                case AvBContract.INSTRUMENTS:
                    c = db.rawQuery("select instrument.instrument_id,instrument.updated_at from instrument_places left join instrument on instrument.instrument_id = instrument_places.instrument_id where PLACE_ID = ? ", new String[]{uri.getPathSegments().get(1)});
                    break;
                case AvBContract.GROUP_QUESTIONS:
                    c = db.rawQuery("select * from instrument left join group_question on instrument.instrument_id = group_question.instrument_id left join question on question.group_id = group_question.group_id " +
                            "where instrument.instrument_id = ? ", new String[]{uri.getPathSegments().get(1)});
                    break;
                case AvBContract.SURVEY:
                    c = db.query(AvBContract.SurveyEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;

                case AvBContract.NEWPLACE:
                    c = db.query("newPlace", projection, selection, selectionArgs, null, null, sortOrder);
                    break;

                case AvBContract.PLACE_CATEGORY:
                    c = db.rawQuery("select * from place_category", null);
                    break;
                case AvBContract.PLACE_TYPES:
                    c = db.rawQuery("select * from place_type where category_id = ? ", new String[]{uri.getPathSegments().get(1)});
                    break;
                case AvBContract.PLACE_PERIOD:
                    c = db.rawQuery("select * from place_period ", null);
                    break;
                case AvBContract.PLACE_PERIODS:
                    Calendar calendar = Calendar.getInstance();
                    c = db.rawQuery("select * from place_period where place_id = ? and day = ? order by status", new String[]{uri.getPathSegments().get(1), String.valueOf(calendar.get(Calendar.DAY_OF_WEEK))});
                    break;

                case AvBContract.ANWSER:
                    c = db.query("anwser", projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case AvBContract.LOCATION:
                    c = db.query("location", projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case AvBContract.LOCATIONS:
                    String queryParam = uri.getPathSegments().get(1);
                    queryParam = "%" + queryParam + "%";
                    c = db.query("location", null, "description like ?", new String[]{queryParam}, null, null, null);
                    //c = db.rawQuery("select * from location where description like ? ", new String[]{queryParam});
                    break;
                case AvBContract.LOCATIONSBYID:
                    c = db.rawQuery("select * from location where idWeb = ? ", new String[]{uri.getPathSegments().get(1)});
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case AvBContract.PLACE:
                return "vnd.android.cursor.dir/vnd." + AvBContract.CONTENT_AUTHORITY + AvBContract.PlaceEntry.TABLE_NAME + " ";
            case AvBContract.PLACE_ID:
                return "vnd.android.cursor.item/vnd." + AvBContract.CONTENT_AUTHORITY + AvBContract.PlaceDetailsEntry.TABLE_NAME + " ";
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
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowID;

        switch (uriMatcher.match(uri)) {
            case AvBContract.PLACE:
                rowID = db.insertWithOnConflict(AvBContract.PlaceEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID == -1) {
                    db.update(AvBContract.PlaceEntry.TABLE_NAME, values, "where PLACE_ID = ?", new String[]{values.getAsString("PLACE_ID")});
                }
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.BASE_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
            case AvBContract.PLACE_DETAILS:
                rowID = db.insertWithOnConflict(AvBContract.PlaceDetailsEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID == -1) {
                    db.update(AvBContract.PlaceDetailsEntry.TABLE_NAME, values, "where PLACE_ID = ?", new String[]{values.getAsString("PLACE_ID")});
                }
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.BASE_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.INSTRUMENT:
                rowID = db.insertWithOnConflict(AvBContract.InstrumentEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.InstrumentEntry.INSTRUMENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.INSTRUMENT_PLACE:
                rowID = db.insertWithOnConflict(AvBContract.InstrumentPlaceEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.InstrumentEntry.INSTRUMENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.GROUP_QUESTION:
                rowID = db.insertWithOnConflict(AvBContract.GroupQuestionEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.GroupQuestionEntry.GROUP_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.QUESTION:
                rowID = db.insertWithOnConflict(AvBContract.QuestionEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.QuestionEntry.QUESTION_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.SURVEY:
                rowID = db.insertWithOnConflict(AvBContract.SurveyEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.SurveyEntry.SURVEY_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.NEWPLACE:
                rowID = db.insertWithOnConflict(AvBContract.NewPlaceEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.NewPlaceEntry.NEWPLACE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
            case AvBContract.PLACE_CATEGORY:
                rowID = db.insertWithOnConflict(AvBContract.PlaceCategoryEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.NewPlaceEntry.NEWPLACE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.PLACE_TYPE:
                rowID = db.insertWithOnConflict(AvBContract.PlaceTypeEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.NewPlaceEntry.NEWPLACE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
            case AvBContract.PLACE_PERIOD:
                rowID = db.insertWithOnConflict(AvBContract.PlacePeriodEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.PlacePeriodEntry.PLACE_PERIOD_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.ANWSER:
                rowID = db.insertWithOnConflict(AvBContract.AnwserEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.AnwserEntry.ANWSER_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

            case AvBContract.LOCATION:
                rowID = db.insertWithOnConflict(AvBContract.LocationEntry.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvBContract.LocationEntry.LOCATION_URI, rowID);
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
        SQLiteDatabase db = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case AvBContract.PLACE:
                count = db.delete(AvBContract.PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case AvBContract.PLACE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(AvBContract.PlaceEntry.TABLE_NAME, "_id" + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case AvBContract.SURVEY:
                count = db.delete(AvBContract.SurveyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case AvBContract.NEWPLACE:
                count = db.delete(AvBContract.NewPlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case AvBContract.ANWSER:
                count = db.delete(AvBContract.AnwserEntry.TABLE_NAME, selection, selectionArgs);
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
        SQLiteDatabase db = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case AvBContract.PLACE:
                count = db.update(AvBContract.PlaceEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AvBContract.PLACE_ID:
                count = db.update(AvBContract.PlaceEntry.TABLE_NAME, values, "_id = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case AvBContract.GROUP_QUESTION:
                count = db.update(AvBContract.GroupQuestionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AvBContract.SURVEY:
                count = db.update(AvBContract.SurveyEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AvBContract.PLACE_PERIOD:
                count = db.update(AvBContract.PlacePeriodEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AvBContract.PLACE_DETAILS:
                count = db.update(AvBContract.PlaceDetailsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AvBContract.ANWSER:
                count = db.update(AvBContract.AnwserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int returnCount = 0;

        switch (uriMatcher.match(uri)) {
            case AvBContract.PLACE:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.PlaceEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (rowID == -1) {
                            db.update(AvBContract.PlaceEntry.TABLE_NAME, value, "where PLACE_ID = ?", new String[]{value.getAsString("PLACE_ID")});
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            continue;
                        }
                        long rowID = db.insertWithOnConflict(AvBContract.InstrumentEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            continue;
                        }
                        long rowID = db.insertWithOnConflict(AvBContract.InstrumentPlaceEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.GroupQuestionEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.QuestionEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.SurveyEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.NewPlaceEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.PlaceCategoryEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long rowID = db.insertWithOnConflict(AvBContract.PlaceTypeEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
            case AvBContract.PLACE_PERIOD:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            continue;
                        }
                        long rowID = db.insertWithOnConflict(AvBContract.PlacePeriodEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
            case AvBContract.ANWSER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            continue;
                        }
                        long rowID = db.insertWithOnConflict(AvBContract.AnwserEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
            case AvBContract.LOCATION:
                db.beginTransaction();
                Log.d(TAG, "bulkInsert size: " + values.length);
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            continue;
                        }
                        long rowID = db.insertWithOnConflict(AvBContract.LocationEntry.TABLE_NAME, "", value, SQLiteDatabase.CONFLICT_REPLACE);
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
