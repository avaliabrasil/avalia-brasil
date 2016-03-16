package org.avaliabrasil.avaliabrasil.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *
 * Use for instanciate all the databases, and get back the right one by request.
 *
 * @version 1.0
 * @since 1.0
 */
public class DatabaseWrapper {

    private static SQLiteOpenHelper sqLiteOpenHelper;

    /**
     * Setup all the databases inside the Application.
     * @param context
     * @return
     *
     * @version 1.0
     * @since 1.0
     */
    public static boolean setUp(@NonNull Context context){
        try{
            sqLiteOpenHelper = new PlaceDatabase(context);
            sqLiteOpenHelper.close();
            sqLiteOpenHelper = new PlaceDetailsDatabase(context);
            sqLiteOpenHelper.close();

            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Set the new database by its {@link DatabaseName} and return to the user,
     * if used the same database many times, better call {@link #getLastDatabase()}
     * @param context
     * @param databaseName
     * @return The selection database.
     * @throws SQLiteException
     * @version 1.0
     * @since 1.0
     */
    public static SQLiteDatabase getDatabase(@NonNull Context context ,@NonNull DatabaseName databaseName) throws SQLiteException{

        close();

        switch(databaseName){
            case PLACE_DATABASE:
                sqLiteOpenHelper = new PlaceDatabase(context);
                return sqLiteOpenHelper.getWritableDatabase();
            case PLACE_DETAILS_DATABASE:
                sqLiteOpenHelper = new PlaceDetailsDatabase(context);
                return sqLiteOpenHelper.getWritableDatabase();
            default:
                throw new SQLiteException("Database not found");
        }
    }

    /**
     * Get the last instantiated {@link SQLiteDatabase}.
     *
     * @return the last opened database, or null if any have been opened.
     * @version 1.0
     * @since 1.0
     */
    @Nullable
    public static SQLiteDatabase getLastDatabase(){
        return sqLiteOpenHelper.getWritableDatabase();
    }

    /**
     * Close the current {@link SQLiteDatabase} if not null.
     *
     * @version 1.0
     * @since 1.0
     */
    public static void close(){
        if(sqLiteOpenHelper != null){
            sqLiteOpenHelper.close();
        }
    }
}
