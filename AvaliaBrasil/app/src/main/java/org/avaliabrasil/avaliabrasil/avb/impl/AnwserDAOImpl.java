package org.avaliabrasil.avaliabrasil.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.annotation.Nullable;
import android.util.Log;

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
        cv.put(AvBContract.AnwserEntry.SURVEY_ID, anwser.getSurveyId());
        cv.put(AvBContract.AnwserEntry.INSTRUMENT_ID, anwser.getInstrumentId());
        cv.put(AvBContract.AnwserEntry.GROUP_ID, anwser.getGroupId());
        cv.put(AvBContract.AnwserEntry.QUESTION_ID, anwser.getQuestion_id());
        cv.put(AvBContract.AnwserEntry.QUESTION_TYPE, !anwser.getNumber().isEmpty() ? "number" : !anwser.getComment().isEmpty() ? "comment" : "likert");
        cv.put(AvBContract.AnwserEntry.ANWSER, !anwser.getNumber().isEmpty() ? anwser.getNumber() : !anwser.getComment().isEmpty() ? anwser.getComment() : anwser.getLikert());

        context.getContentResolver().insert(AvBContract.AnwserEntry.ANWSER_URI, cv);
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
    public List<Anwser> getUnsendedAnwsersBySurveyId(String surveyId) {
        ArrayList<Anwser> anwsers = new ArrayList<>();

        Cursor c = context.getContentResolver().query(AvBContract.AnwserEntry.ANWSER_URI, null, "survey_id = ?", new String[]{surveyId}, null);

        Anwser anwser;
        while(c.moveToNext()){
            anwser = new Anwser(c);
            anwsers.add(anwser);
        }

        c.close();
        return anwsers;
    }

    @Override
    @Nullable
    public Anwser getLastAnwserBySurveyId(String surveyId){
        Cursor c = context.getContentResolver().query(AvBContract.AnwserEntry.ANWSER_URI, null, "survey_id = ?", new String[]{surveyId}, "_id desc");

        if(c.moveToNext()){
            return new Anwser(c);
        }
        return null;
    }
}
