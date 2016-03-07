package org.avaliabrasil.avaliabrasil.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.avaliabrasil.avaliabrasil.data.AvBContract.PlaceEntry;


public class AvBDBHelper extends SQLiteOpenHelper {
    public final String LOG_TAG = this.getClass().getSimpleName();

    private static final int DATABASE_VERSION = 5;
    static final String DATABASE_NAME = "avaliabrasil.db";

    public AvBDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase) {

        // Criando Tabela Places

        final String SQL_CREATE_PLACE_TABLE =
                "CREATE TABLE " + PlaceEntry.TABLE_NAME + " (" +

                        // AvaliaBrasil MySql Entries
                        PlaceEntry._ID + " INTEGER PRIMARY KEY," +
                        PlaceEntry.COLUMN_PLACE_ID + " TEXT UNIQUE NOT NULL, " +
                        PlaceEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                        // Local-Only Columns
                        PlaceEntry.COLUMN_ADRESS + " TEXT, " +
                        PlaceEntry.COLUMN_PHONE + " TEXT, " +
                        PlaceEntry.COLUMN_EMAIL + " TEXT, " +
                        PlaceEntry.COLUMN_OPEN_HOURS + " TEXT" +
                        " );";
        // Rodando as Instruções SQL para criar tabelas
        sqLiteDatabase.execSQL(SQL_CREATE_PLACE_TABLE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // TODO: Rever política atual de atualização da base de dados! Deste modo ele irá excluir tudo!
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceEntry.TABLE_NAME);

        // Recriando a tabela!
        onCreate(sqLiteDatabase);
    }

}
