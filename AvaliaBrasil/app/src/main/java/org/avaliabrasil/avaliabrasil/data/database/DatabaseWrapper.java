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

//Modificar para o padrão do google
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
            sqLiteOpenHelper = new DatabaseHelper(context);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set the new database by its {@link DatabaseName} and return to the user,
     * if used the same database many times, better call {@link #getLastDatabase()}
     * @param context
     * @return The selection database.
     * @throws SQLiteException
     * @version 1.0
     * @since 1.0
     */
    public static SQLiteDatabase getDatabase(@NonNull Context context) throws SQLiteException{

        return sqLiteOpenHelper.getWritableDatabase();
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
