package org.avaliabrasil.avaliabrasil.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.GroupQuestionDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.InstrumentDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.NewPlaceDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.QuestionDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.SurveyDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.GroupQuestion;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Instrument;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Survey;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 09/05/2016.
 */
public class SurveyDAOImpl implements SurveyDAO {

    private Context context;
    private InstrumentDAO instrumentDAO;
    private GroupQuestionDAO groupQuestionDAO;
    private QuestionDAO questionDAO;
    private NewPlaceDAO newPlaceDAO;
    private AnwserDAO anwserDAO;
    public SurveyDAOImpl(Context context,InstrumentDAO instrumentDAO,GroupQuestionDAO groupQuestionDAO,QuestionDAO questionDAO,
                         NewPlaceDAO newPlaceDAO, AnwserDAO anwserDAO) {
        this.context = context;
        this.instrumentDAO = instrumentDAO;
        this.groupQuestionDAO = groupQuestionDAO;
        this.questionDAO = questionDAO;
        this.newPlaceDAO = newPlaceDAO;
        this.anwserDAO = anwserDAO;
    }

    @Override
    public boolean boundPlaceAndInstrument(String placeId, Survey survey) throws SQLException {
            ContentValues[] values = new ContentValues[survey.getInstruments().size()];
            ContentValues value = null;

            for (int i = 0; i < values.length; i++) {
                if (survey.getInstruments().get(i).getGroupQuestions().size() != 0) {
                    value = new ContentValues();
                    value.put(AvBContract.InstrumentPlaceEntry.INSTRUMENT_ID, survey.getInstruments().get(i).getId());
                    value.put(AvBContract.InstrumentPlaceEntry.PLACE_ID, placeId);
                    values[i] = value;
                }
            }

            context.getContentResolver().bulkInsert(
                    AvBContract.InstrumentPlaceEntry.INSTRUMENTPLACE_URI, values);
            return true;
    }

    @Override
    public boolean bulkAddInstrument(Survey survey) throws SQLException {
        return instrumentDAO.bulkAddInstrument(survey.getInstruments());
    }

    @Override
    public boolean bulkAddQuestionGroup(Survey survey) throws SQLException {
        for(Instrument instru : survey.getInstruments()){
            groupQuestionDAO.bulkQuestionGroup(instru.getId(),instru.getGroupQuestions());
        }
        return true;
    }

    @Override
    public boolean bulkAddQuestion(Survey survey) throws SQLException {
        for(Instrument instru : survey.getInstruments()){
           for(GroupQuestion groupQuestion : instru.getGroupQuestions()){
               questionDAO.bulkInsertQuestion(groupQuestion.getId(),groupQuestion.getQuestions());
           }
        }
        return true;
    }

    @Override
    @Nullable
    public Survey findSurveyByPlaceId(String placeId) throws SQLException {
        Cursor c = context.getContentResolver().query(
                AvBContract.PlaceEntry.getPlaceDetails(placeId), null, null, null, null);

        Survey survey = new Survey();
        survey.setPlaceId(placeId);
        List<Instrument> instruments = new ArrayList<Instrument>();
        survey.setInstruments(instruments);
        if (c.moveToNext()) {
            List<String> listOfInstrumentIds = instrumentDAO.getInstrumentIdListByPlace(placeId);

            for (String instrumentId : listOfInstrumentIds) {
                instruments.add(new Instrument(instrumentId,"", groupQuestionDAO.findGroupByInstrumentId(instrumentId)));
            }
        }
        c.close();
        return survey;
    }

    @Override
    public void removePendingSurvey() {
        Cursor c = context.getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");

        if(c.moveToNext()){
            if(newPlaceDAO == null){
                throw new IllegalArgumentException("NewPlaceDAO can't be null!");
            }
            newPlaceDAO.deleteNewPlaceByPlaceId(c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID)));
            anwserDAO.deleteSendedSurvey();
        }
        c.close();
    }

    private Cursor getPendingSurveyCursor(){
        return  context.getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");
    }

    @Override
    public boolean checkIfThereIsPendingSurvey() {
        Cursor c = getPendingSurveyCursor();

        if(c.moveToNext()){
            c.close();
            return true;
        }else{
            c.close();
            return false;
        }
    }

    @Override
    @Nullable
    public Survey findPendingSurvey() {
        Cursor c = context.getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");

        c.moveToNext();

        String placeId = c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID));
        c.close();

        try {
            return findSurveyByPlaceId(placeId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
