package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.InstrumentDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Instrument;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer on 09/05/2016.
 */
public class InstrumentDAOImpl implements InstrumentDAO {

    private Context context;

    public InstrumentDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean bulkAddInstrument(List<Instrument> instruments){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        Date dateAtual = new Date(System.currentTimeMillis());

        ContentValues[] values = new ContentValues[instruments.size()];
        ContentValues value = null;

        for (int i = 0; i < instruments.size(); i++) {
            if (instruments.get(i).getGroupQuestions().size() != 0) {
                value = new ContentValues();
                value.put(AvBContract.InstrumentEntry.INSTRUMENT_ID, instruments.get(i).getId());
                value.put(AvBContract.InstrumentEntry.UPDATED_AT, dateFormat.format(dateAtual));
                values[i] = value;
            }
        }

        context.getContentResolver().bulkInsert(
                AvBContract.InstrumentEntry.INSTRUMENT_URI, values);

        return true;
    }

    @Override
    public boolean updateInstrument(Instrument instrument) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");
        Date dateAtual = new Date(System.currentTimeMillis());

        ContentValues value = new ContentValues();
        value.put(AvBContract.InstrumentEntry.INSTRUMENT_ID, instrument.getId());
        value.put(AvBContract.InstrumentEntry.UPDATED_AT, dateFormat.format(dateAtual));
        context.getContentResolver().update(
                AvBContract.InstrumentEntry.INSTRUMENT_URI,value,AvBContract.InstrumentEntry.INSTRUMENT_ID + " = ?",new String[]{instrument.getId()});
        return false;
    }

    @Override
    public List<String> getInstrumentIdListByPlace(String placeId) throws SQLException {

        Cursor c = context.getContentResolver().query(AvBContract.InstrumentEntry.findSurveyByPlaceUri(placeId), null, null, null, null);

        List<String> listOfInstrumentIds = new ArrayList<String>();

        while (c.moveToNext()) {
            listOfInstrumentIds.add(c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
        }

        c.close();

        return listOfInstrumentIds;
    }
}
