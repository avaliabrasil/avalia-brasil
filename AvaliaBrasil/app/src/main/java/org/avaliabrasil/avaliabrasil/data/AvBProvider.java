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

    public static final String PLACE_PATH = "places";

    static final int PLACE = 1;
    static final int PLACE_ID = 2;
    static final int PLACEDETAILS = 3;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, "places", PLACE);
        uriMatcher.addURI(CONTENT_AUTHORITY, "places/*", PLACE_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, "placesdetails", PLACEDETAILS);
    }

    public static Uri getPlaceDetails(String place_id){
        Uri uri = Uri.parse("content://"+CONTENT_AUTHORITY+"/places/" + place_id);
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

        SQLiteDatabase db = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor c;

        switch (uriMatcher.match(uri)) {
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                qb.setTables(DatabaseHelper.place_table);

                if(selectionArgs != null){
                    c = qb.query(db,projection,	"name like ?", selectionArgs,null, null, sortOrder);
                }else{
                    c = qb.query(db,projection,	selection, selectionArgs,null, null, sortOrder);
                }

                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case PLACE_ID:
                db = DatabaseWrapper.getDatabase(getContext());
                c = db.rawQuery("select * from place_detail LEFT OUTER join place on place_detail.place_id = place.place_id where place_detail.place_id = ?",new String[]{ uri.getPathSegments().get(1)});
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
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = null;

        switch (uriMatcher.match(uri)) {
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext());
                db.beginTransaction();
                int returnCount = 0;
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
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
