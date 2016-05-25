package org.avaliabrasil.avaliabrasil.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Anwser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 12/05/2016.
 */
public class AnwserDAOImpl implements AnwserDAO{

    private Context context;

    public AnwserDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public void insertAnwser(Anwser anwser) {
        ContentValues cv = new ContentValues();

        cv.put(AvBContract.SurveyEntry.PLACE_ID, anwser.getPlaceId());
        cv.put(AvBContract.SurveyEntry.INSTRUMENT_ID, anwser.getInstrumentId());
        cv.put(AvBContract.SurveyEntry.GROUP_ID, anwser.getGroupId());

        cv.put(AvBContract.SurveyEntry.QUESTION_ID, anwser.getQuestion_id());
        cv.put(AvBContract.SurveyEntry.QUESTION_TYPE, !anwser.getNumber().isEmpty() ? "number" : !anwser.getComment().isEmpty() ? "comment" : "likert");
        cv.put(AvBContract.SurveyEntry.ANWSER, !anwser.getNumber().isEmpty() ? anwser.getNumber() : !anwser.getComment().isEmpty() ? anwser.getComment() : anwser.getLikert());

        context.getContentResolver().insert(AvBContract.SurveyEntry.SURVEY_URI, cv);
    }

    @Override
    public void deleteAllAnwserByPlaceId(String placeId) {
    }

    @Override
    public void setSurveyAsCompleted() {
        ContentValues cv = new ContentValues();

        cv.put(AvBContract.SurveyEntry.SURVEY_FINISHED, true);

        context.getContentResolver().update(AvBContract.SurveyEntry.SURVEY_URI, cv, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ? ", new String[]{"false"});

    }

    @Override
    public void deleteSendedSurvey() {
        context.getContentResolver().delete(AvBContract.SurveyEntry.SURVEY_URI, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"});
    }

    @Override
    public List<Anwser> getUnsendedAnwsers() {
        ArrayList<Anwser> anwsers = new ArrayList<>();

        Cursor c = context.getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id asc");
        Anwser anwser;
        while(c.moveToNext()){
            anwser = new Anwser(c);
            anwsers.add(anwser);
        }

        c.close();
        return anwsers;
    }
}
