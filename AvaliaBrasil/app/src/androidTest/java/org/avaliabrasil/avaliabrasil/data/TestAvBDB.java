package org.avaliabrasil.avaliabrasil.data;

import android.test.AndroidTestCase;

/**
 * Created by Pedro on 22/02/2016.
 */
public class TestAvBDB extends AndroidTestCase {
    /*public static final String LOG_TAG = TestAvBDB.class.getSimpleName();

    void deleteTheDatabase(){
        mContext.deleteDatabase(AvBDBHelper.DATABASE_NAME);
    }

    public void setUp(){
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();

        // Adicionar outras tabelas aqui!
        tableNameHashSet.add(AvBContract.PlaceEntry.TABLE_NAME);


        mContext.deleteDatabase(AvBDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new AvBDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + AvBContract.PlaceEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> placeColumnHashSet = new HashSet<>();
        placeColumnHashSet.add(AvBContract.PlaceEntry._ID);
        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_PLACE_ID);
        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_NAME);
        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_DATE_TIME);
        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_STATUS);

        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_ADRESS);
        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_PHONE);
        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_EMAIL);
        placeColumnHashSet.add(AvBContract.PlaceEntry.COLUMN_OPEN_HOURS);

        //int columnNameIndex = c.getColumnIndex("name");
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            placeColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                placeColumnHashSet.isEmpty());
        db.close();
    }

    public long testPlaceTable(){
        AvBDBHelper avBDBHelper = new AvBDBHelper(mContext);

        // Pegando a base
        SQLiteDatabase db = avBDBHelper.getWritableDatabase();

        // Criando conteúdo fake
        ContentValues testValues = TestUtilities.createPlaceValues();

        long locationRowId;
        // Insere os Dados
        locationRowId = db.insert(AvBContract.PlaceEntry.TABLE_NAME, null, testValues);

        // Verifica se retornou algum id!
        assertTrue(locationRowId != -1);

        // Fazendo uma query na Base!
        Cursor cursor = db.query(AvBContract.PlaceEntry.TABLE_NAME,// Tabela
                null, // Al columns!
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
                );

        // Verificar se chegou algum resultado!
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Verificar os dados do Cursor com os dados Originais

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);

        // Verificar se existe só este item na base de dados
        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());

        // Fechando cursor e base de dados

        cursor.close();
        db.close();
        return locationRowId;
    }*/

}
