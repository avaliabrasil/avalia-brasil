package org.avaliabrasil.avaliabrasil.data;


import android.annotation.TargetApi;
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

import org.avaliabrasil.avaliabrasil.data.database.DatabaseName;
import org.avaliabrasil.avaliabrasil.data.database.DatabaseWrapper;
import org.avaliabrasil.avaliabrasil.data.database.PlaceDatabase;
import org.avaliabrasil.avaliabrasil.data.database.PlaceDetailsDatabase;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceDetails;

/**
 * Created by Pedro on 23/02/2016.
 */
public class AvBProviderTest extends ContentProvider {
    public final String LOG_TAG = this.getClass().getSimpleName();

    public static final String CONTENT_AUTHORITY = "org.avaliabrasil.avaliabrasil";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final Uri PLACE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY+"/places");

    public static final String PLACE_PATH = "places";

    static final int PLACE = 1;
    static final int PLACE_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, "places", PLACE);
        uriMatcher.addURI(CONTENT_AUTHORITY, "places/*", PLACE_ID);
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

        switch (uriMatcher.match(uri)) {
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DATABASE);
                qb.setTables(PlaceDatabase.databaseName);
                break;
            case PLACE_ID:
                qb.appendWhere( "_id =" + uri.getPathSegments().get(1));
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DETAILS_DATABASE);
                qb.setTables(PlaceDetailsDatabase.databaseName);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);


        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case PLACE:
                return "vnd.android.cursor.dir/vnd."+ CONTENT_AUTHORITY + PlaceDatabase.databaseName  +" ";

            case PLACE_ID:
                return "vnd.android.cursor.item/vnd."+ CONTENT_AUTHORITY + PlaceDetailsDatabase.databaseName  +" ";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = null;

        switch (uriMatcher.match(uri)){
            case PLACE:
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DATABASE);
                long rowID = db.insert(	PlaceDatabase.databaseName, "", values);
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
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DATABASE);
                count = db.delete(PlaceDatabase.databaseName, selection, selectionArgs);
                break;
            case PLACE_ID:
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DATABASE);
                String id = uri.getPathSegments().get(1);
                count = db.delete( PlaceDatabase.databaseName, "_id" +  " = " + id +
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
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DATABASE);
                count = db.update( PlaceDatabase.databaseName, values, selection, selectionArgs);
                break;
            case PLACE_ID:
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DATABASE);

                count = db.update( PlaceDatabase.databaseName, values, "_id = " + uri.getPathSegments().get(1) +
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
                db = DatabaseWrapper.getDatabase(getContext(), DatabaseName.PLACE_DATABASE);
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        if(db.rawQuery("select place_id from place where place_id = ? " ,new String[]{value.getAsString("place_id")}).getCount() > 0){
                            continue;
                        }
                        long rowID = db.insert(	PlaceDatabase.databaseName, "", value);
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
